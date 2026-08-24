package com.orderflow.saga.client.inventory;

public record InventoryReserveResponse(String sku, int reservedQty, int availableQty) {
}
