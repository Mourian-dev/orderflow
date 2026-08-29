package com.orderflow.saga.client.fraudCheck;

import java.math.BigDecimal;
import java.util.List;

public record FraudCheckRequest(Long orderId, String customerId, List<FraudCheckCartItem> cart, BigDecimal amount) {
}
