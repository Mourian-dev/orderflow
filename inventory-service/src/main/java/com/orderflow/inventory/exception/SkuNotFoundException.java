package com.orderflow.inventory.exception;

public class SkuNotFoundException extends RuntimeException {
    public SkuNotFoundException(String sku) {
        super("No Inventory row for SKU: " + sku);
    }
}
