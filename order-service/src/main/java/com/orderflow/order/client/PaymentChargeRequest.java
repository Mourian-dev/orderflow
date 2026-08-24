package com.orderflow.order.client;

import java.math.BigDecimal;

public record PaymentChargeRequest(Long orderId, BigDecimal amount) { }
