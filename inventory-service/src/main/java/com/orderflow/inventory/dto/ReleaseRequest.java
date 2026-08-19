package com.orderflow.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ReleaseRequest(
        @NotBlank(message = "SKU is required") String sku,
        @Positive(message = "Qty must be positive") int qty
) {
}
