package com.orderflow.order.event;

public record OrderCancelledEvent(Long orderId, String sku, int qty, String chargeId) { }
