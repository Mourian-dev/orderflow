package com.orderflow.saga.client.payment;

import java.math.BigDecimal;

public record PaymentReverseResponse(String chargeId, Long orderId, BigDecimal amount, String status) { }
