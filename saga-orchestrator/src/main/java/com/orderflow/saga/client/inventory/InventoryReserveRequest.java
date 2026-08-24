package com.orderflow.saga.client.inventory;

public record InventoryReserveRequest(String sku, int qty) {
}
