package com.orderflow.payment.exception;


public class ChargeNotFoundException extends RuntimeException {
    public ChargeNotFoundException(String chargeId) {
        super("Payment not found for this charge Id : " + chargeId);
    }
}
