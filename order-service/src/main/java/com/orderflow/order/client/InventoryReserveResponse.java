package com.orderflow.order.client;

public record InventoryReserveResponse(String sku, int reservedQty, int availableQty) {
}
