package com.orderflow.order.exception;

import com.orderflow.order.entity.OrderStatus;

public class IllegalOrderStateException extends RuntimeException {
    public IllegalOrderStateException(Long orderId, OrderStatus current, OrderStatus attempted) {
        super("Order " + orderId + " cannot transition from " + current + " to " + attempted + " -- only a PENDING order can be confirmed or marked failed");
    }
}
