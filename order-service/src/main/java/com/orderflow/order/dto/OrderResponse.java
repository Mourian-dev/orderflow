package com.orderflow.order.dto;

import com.orderflow.order.entity.Order;

import java.math.BigDecimal;

public record OrderResponse(
        Long id,
        String sku,
        int qty,
        String customerId,
        BigDecimal amount,
        String status
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(), order.getSku(), order.getQty(),
                order.getCustomerId(), order.getAmount(), order.getStatus().name()
        );
    }
}
