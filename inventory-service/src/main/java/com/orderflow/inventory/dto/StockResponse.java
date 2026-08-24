package com.orderflow.inventory.dto;

public record StockResponse(String sku, int totalQty, int reservedQty, int availableQty) { }
