package com.orderflow.payment.service;

import com.orderflow.payment.dto.ReverseRequest;
import com.orderflow.payment.entity.ProcessedEvent;
import com.orderflow.payment.event.OrderCancelledEvent;
import com.orderflow.payment.repository.ProcessedEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCancellationHandler {
    private final PaymentService paymentService;
    private final ProcessedEventRepository repository;

    @Transactional
    public void handle(OrderCancelledEvent event) {
        if(repository.existsById(event.orderId())) {
            log.info("order.cancelled for order {} already processed by payment-service -- skipping reverse", event.orderId());
            return;
        }
        paymentService.reverse(new ReverseRequest(event.chargeId()));
        repository.save(new ProcessedEvent(event.orderId(), Instant.now()));
    }
}
