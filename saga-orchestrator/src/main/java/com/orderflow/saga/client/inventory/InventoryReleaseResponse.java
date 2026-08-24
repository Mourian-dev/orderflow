package com.orderflow.saga.client.inventory;

public record InventoryReleaseResponse(String sku, int reservedQty, int availableQty) {
}
