package com.orderflow.order.service;

import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.entity.Order;
import com.orderflow.order.entity.OrderStatus;
import com.orderflow.order.event.OrderCancelledEvent;
import com.orderflow.order.event.OrderCancelledEventPublisher;
import com.orderflow.order.exception.IllegalOrderStateException;
import com.orderflow.order.repository.OrderRepository;
import com.orderflow.order.exception.OrderNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final OrderRepository repository;
    private final OrderCancelledEventPublisher eventPublisher;

    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setSku(request.sku());
        order.setAmount(request.amount());
        order.setQty(request.qty());
        order.setCustomerId(request.customerId());
        order.setStatus(OrderStatus.PENDING);
        return repository.save(order);
    }

    public Order confirm(Long orderId, String chargeId) {
        Order order = repository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if(order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalOrderStateException(orderId, order.getStatus(), OrderStatus.CONFIRMED);
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setChargeId(chargeId);
        return repository.save(order);
    }

    public Order markFailed(Long orderId, String reason) {
        Order order = repository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if(order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalOrderStateException(orderId, order.getStatus(), OrderStatus.FAILED);
        }

        order.setStatus(OrderStatus.FAILED);
        return repository.save(order);
    }

    @Transactional
    public Order cancel(Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if(order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalOrderStateException(id, order.getStatus(), OrderStatus.CANCELLING);
        }

        order.setStatus(OrderStatus.CANCELLING);
        Order saved = repository.save(order);

        eventPublisher.publish(new OrderCancelledEvent(saved.getId(), saved.getSku(), saved.getQty(), saved.getChargeId()));
        return saved;
    }

    @Transactional
    public void markStockReleased(Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if(order.getStatus() != OrderStatus.CANCELLING) {
            log.info("Order {} received StockReleased but is not CANCELLING (status={}) -- ignoring, likely a redelivery",
                    id, order.getStatus());
        }

        order.setStockReleased(true);
        completeIfBothReceived(order);
        repository.save(order);
    }

    @Transactional
    public void markPaymentRefunded(Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if(order.getStatus() != OrderStatus.CANCELLING) {
            log.info("Order {} received PaymentRefunded but is not CANCELLING (status={}) -- ignoring, likely a redelivery",
                    id, order.getStatus());
        }

        order.setPaymentRefunded(true);
        completeIfBothReceived(order);
        repository.save(order);
    }

    private void completeIfBothReceived(Order order) {
        if(Boolean.TRUE.equals(order.getStockReleased()) && Boolean.TRUE.equals(order.getPaymentRefunded())) {
            order.setStatus(OrderStatus.CANCELLED);
            log.info("Order {} fully cancelled -- StockReleased and PaymentRefunded both received", order.getId());
        }
    }

    public Order getOrder(Long orderId) {
        return repository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public List<Order> list() {
        return repository.findAll();
    }

    public void delete(Long orderId) {
        if(repository.existsById(orderId))
            repository.deleteById(orderId);
        else throw new OrderNotFoundException(orderId);
    }
}
