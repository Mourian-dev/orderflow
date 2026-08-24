package com.orderflow.order.service;

import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.entity.Order;
import com.orderflow.order.entity.OrderStatus;
import com.orderflow.order.exception.IllegalOrderStateException;
import com.orderflow.order.repository.OrderRepository;
import com.orderflow.order.exception.OrderNotFoundException;
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
    private final RestTemplate restTemplate;

    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setSku(request.sku());
        order.setAmount(request.amount());
        order.setQty(request.qty());
        order.setCustomerId(request.customerId());
        order.setStatus(OrderStatus.PENDING);
        return repository.save(order);
    }

    public Order confirm(Long orderId) {
        Order order = repository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if(order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalOrderStateException(orderId, order.getStatus(), OrderStatus.CONFIRMED);
        }

        order.setStatus(OrderStatus.CONFIRMED);
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
