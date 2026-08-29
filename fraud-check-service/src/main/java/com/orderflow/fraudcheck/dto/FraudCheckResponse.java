package com.orderflow.fraudcheck.dto;

public record FraudCheckResponse(
        Long orderId,
        String decision,
        double similarityScore,
        String matchedPattern,
        String reasoning
) {
}
