package com.orderflow.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String sku, int requestedQty, int availableQty) {
        super("Insufficient stock for SKU " + sku + ": requested " + requestedQty + " but only " + availableQty + " available");
    }
}
