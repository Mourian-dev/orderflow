package com.orderflow.inventory.dto;

import com.orderflow.inventory.entity.Reservation;

public record ReserveResponse(
        String sku,
        int reservedQty,
        int availableQty
) { }
