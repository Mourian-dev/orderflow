package com.orderflow.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ChargeRequest(
        @NotNull(message = "OrderId should not be empty") Long orderId,
        @Positive(message = "Amount must be positive") BigDecimal amount
) {

}
