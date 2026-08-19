package com.orderflow.inventory.dto;

public record ReleaseResponse(
        String sku,
        int reservedQty,
        int availableQty
) {
}
