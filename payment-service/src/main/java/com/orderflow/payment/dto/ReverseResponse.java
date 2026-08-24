package com.orderflow.payment.dto;

import com.orderflow.payment.entity.Payment;

import java.math.BigDecimal;

public record ReverseResponse(
        String chargeId,
        Long orderId,
        BigDecimal amount,
        String status
) {
    public static ReverseResponse from(Payment payment) {
        return new ReverseResponse(payment.getChargeId(), payment.getOrderId(), payment.getAmount(),payment.getStatus());
    }
}
