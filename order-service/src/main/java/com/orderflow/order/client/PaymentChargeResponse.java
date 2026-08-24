package com.orderflow.order.client;

import java.math.BigDecimal;

public record PaymentChargeResponse(String chargeId, Long orderId, BigDecimal amount, String status) { }
