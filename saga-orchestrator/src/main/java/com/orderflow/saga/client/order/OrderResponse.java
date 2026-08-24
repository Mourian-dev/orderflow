package com.orderflow.saga.client.order;

import java.math.BigDecimal;

public record OrderResponse(Long id, String sku, int qty, String customerId, BigDecimal amount, String status) { }
