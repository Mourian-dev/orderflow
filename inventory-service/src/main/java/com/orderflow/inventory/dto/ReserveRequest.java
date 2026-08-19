package com.orderflow.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ReserveRequest(
        @NotBlank(message = "SKU is required") String sku,
        @Positive(message = "qty must be positive") int qty
) { }
