package com.orderflow.order.controller;

import com.orderflow.order.dto.ConfirmRequest;
import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.MarkFailedRequest;
import com.orderflow.order.dto.OrderResponse;
import com.orderflow.order.entity.Order;
import com.orderflow.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        Order created = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(created));
    }

    @PutMapping("/{id}/confirm")
    public OrderResponse confirm(@PathVariable Long id, @Valid @RequestBody ConfirmRequest request) {
        return OrderResponse.from(orderService.confirm(id, request.chargeId()));
    }

    @PutMapping("/{id}/fail")
    public OrderResponse fail(@PathVariable Long id, MarkFailedRequest request) {
        return OrderResponse.from(orderService.markFailed(id, request.reason()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable  Long id) {
        Order cancelling = orderService.cancel(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(OrderResponse.from(cancelling));
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return OrderResponse.from(orderService.getOrder(id));
    }

    @GetMapping
    public List<OrderResponse> list() {
        return orderService.list().stream().map(OrderResponse::from).toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
