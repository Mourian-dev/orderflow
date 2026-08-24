package com.orderflow.saga.client.inventory;

public record InventoryReleaseRequest(String sku, int qty) {
}
