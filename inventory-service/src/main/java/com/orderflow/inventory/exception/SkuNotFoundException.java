package com.orderflow.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SkuNotFoundException extends RuntimeException {
    public SkuNotFoundException(String sku) {
        super("No Inventiry row for SKU: " + sku);
    }
}
