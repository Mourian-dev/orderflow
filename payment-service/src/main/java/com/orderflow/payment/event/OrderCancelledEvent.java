package com.orderflow.payment.event;

public record OrderCancelledEvent(Long orderId, String sku, int qty, String chargeId) { }
