package com.orderflow.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest (
        @NotBlank(message = "sku is required") String sku,
        @Positive(message = "qty must be positive") int qty,
        @NotBlank(message = "customer id is required") String customerId,
        @Positive(message = "amount must be positive") BigDecimal amount
) {}
