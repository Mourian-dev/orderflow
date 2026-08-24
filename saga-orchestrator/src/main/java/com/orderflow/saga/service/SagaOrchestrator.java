package com.orderflow.saga.service;

import com.orderflow.saga.client.fraudCheck.FraudCheckClient;
import com.orderflow.saga.client.fraudCheck.FraudDecision;
import com.orderflow.saga.client.inventory.InventoryClient;
import com.orderflow.saga.client.inventory.InventoryReserveResponse;
import com.orderflow.saga.client.order.OrderClient;
import com.orderflow.saga.client.order.OrderResponse;
import com.orderflow.saga.client.payment.PaymentChargeResponse;
import com.orderflow.saga.client.payment.PaymentClient;
import com.orderflow.saga.dto.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaOrchestrator {
    private final OrderClient orderClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final FraudCheckClient fraudCheckClient;
    private final Executor sagaExecutor;

    public OrderResponse run(CreateOrderCommand cmd) {
        OrderResponse orderResponse = orderClient.create(cmd);
        log.info("Order Created:: id = {} status = {}", orderResponse.id(), orderResponse.status());

        Deque<Runnable> compensations = new ArrayDeque<>();

        try {
            CompletableFuture<FraudDecision> fraudFuture = CompletableFuture.supplyAsync(() -> fraudCheckClient.evaluate(cmd), sagaExecutor);
            CompletableFuture<InventoryReserveResponse> inventoryFuture = CompletableFuture.supplyAsync(() -> inventoryClient.reserve(cmd.sku(), cmd.qty()), sagaExecutor);

            FraudDecision fraudDecision;

            try {
                CompletableFuture.allOf(fraudFuture, inventoryFuture).join();
                fraudDecision = fraudFuture.join();
                inventoryFuture.join();
                compensations.push(() -> inventoryClient.release(orderResponse.sku(), orderResponse.qty()));
            } catch(CompletionException e) {
                return failOrder(orderResponse.id(), "inventory_unavailable: " + rootMessage(e));
            }

            if(fraudDecision != FraudDecision.APPROVE) {
                runCompensation(compensations);
                return failOrder(orderResponse.id(), "fraud_declined");
            }

            PaymentChargeResponse charge;
            try {
                charge = paymentClient.charge(orderResponse.id(), cmd.amount());
                compensations.push(() ->paymentClient.reverse(charge.chargeId()));
            } catch(RestClientException e) {
                runCompensation(compensations);
                return failOrder(orderResponse.id(), "Payment_failed: " + rootMessage(e));
            }

            try {
                OrderResponse confirmed = orderClient.confirm(orderResponse.id());
                log.info("Order confirmed:: id = {} status = {}", confirmed.id(), confirmed.status());
                return confirmed;
            } catch(RestClientException e) {
                runCompensation(compensations);
                return failOrder(orderResponse.id(), "confirm_failed :" + rootMessage(e));
            }
        } catch(RuntimeException e) {
            runCompensation(compensations);
            throw e;
        }
    }

    private void runCompensation(Deque<Runnable> compensations) {
        while(!compensations.isEmpty()) {
            compensations.pop().run();
        }
    }

    private OrderResponse failOrder(Long orderId, String reason) {
        log.warn("Saga failed for order {}:: {}", orderId, reason);
        return orderClient.markFailed(orderId, reason);
    }

    private String rootMessage(Throwable t) {
        Throwable cause = t.getCause() != null ? t.getCause() : t;
        return cause.getMessage();
    }
}
