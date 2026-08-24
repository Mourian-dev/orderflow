package com.orderflow.payment.dto;

import com.orderflow.payment.entity.Payment;

import java.math.BigDecimal;

public record ChargeResponse(
        String chargeId,
        Long orderId,
        BigDecimal amount,
        String status
) {
    public static ChargeResponse from(Payment payment) {
        return new ChargeResponse(payment.getChargeId(), payment.getOrderId(), payment.getAmount(), payment.getStatus());
    }
}
