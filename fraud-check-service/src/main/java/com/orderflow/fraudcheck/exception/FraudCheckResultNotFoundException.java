package com.orderflow.fraudcheck.exception;

public class FraudCheckResultNotFoundException extends RuntimeException {
    public FraudCheckResultNotFoundException(Long orderId) {
        super("No fraud-check result found for order " + orderId);
    }
}
