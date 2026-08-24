package com.orderflow.saga.controller;

import com.orderflow.saga.client.order.OrderResponse;
import com.orderflow.saga.dto.CreateOrderCommand;
import com.orderflow.saga.service.SagaOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class SagaOrchestratorController {

    private final SagaOrchestrator orchestrator;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderCommand cmd) {
        OrderResponse response = orchestrator.run(cmd);
        HttpStatus status = "CONFIRMED".equals(response.status()) ? HttpStatus.CREATED : HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity.status(status).body(response);
    }
}
