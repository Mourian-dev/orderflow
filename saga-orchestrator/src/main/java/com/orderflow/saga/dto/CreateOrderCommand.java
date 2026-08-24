package com.orderflow.saga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderCommand(
        @NotBlank(message = "SKU is required") String sku,
        @Positive(message = "Qty should be positive") int qty,
        @NotBlank(message = "CustomerId is required") String customerId,
        @Positive(message = "Amount should be positive") BigDecimal amount
) {
}
