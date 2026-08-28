package com.orderflow.inventory.event;

public record OrderCancelledEvent(Long orderId, String sku, int qty, String chargeId) {
}
