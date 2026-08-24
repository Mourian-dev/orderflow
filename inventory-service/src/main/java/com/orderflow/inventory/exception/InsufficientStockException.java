package com.orderflow.inventory.exception;

import lombok.Getter;

@Getter
public class InsufficientStockException extends RuntimeException {

    private final String sku;
    private final int requestedQty;
    private final int availableQty;

    public InsufficientStockException(String sku, int requestedQty, int availableQty) {
        super("Insufficient stock for SKU " + sku + ": requested " + requestedQty + " but only " + availableQty + " available");
        this.sku = sku;
        this.requestedQty = requestedQty;
        this.availableQty = availableQty;
    }
}
