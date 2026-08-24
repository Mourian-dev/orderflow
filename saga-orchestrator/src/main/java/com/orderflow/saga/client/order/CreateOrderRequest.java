package com.orderflow.saga.client.order;

import java.math.BigDecimal;

public record CreateOrderRequest(String sku, int qty, String customerId, BigDecimal amount) { }
