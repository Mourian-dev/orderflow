package com.orderflow.saga.client.payment;

import java.math.BigDecimal;

public record PaymentChargeRequest(Long orderId, BigDecimal amount) { }
