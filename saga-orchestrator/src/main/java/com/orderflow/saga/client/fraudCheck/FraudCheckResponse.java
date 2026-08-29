package com.orderflow.saga.client.fraudCheck;

public record FraudCheckResponse(Long orderId, String decision, double similarityScore, String matchedPattern, String reasoning) { }
