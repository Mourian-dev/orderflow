package com.orderflow.fraudcheck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record FraudCheckRequest(
        @NotNull(message = "OrderId is required") Long orderId,
        @NotBlank(message = "Customer Id is required") String customerId,
        @NotEmpty(message = "cart must contain at least one item") List<CartItem> cart,
        @Positive(message = "amount should be greater than 0") BigDecimal amount
) {
}
