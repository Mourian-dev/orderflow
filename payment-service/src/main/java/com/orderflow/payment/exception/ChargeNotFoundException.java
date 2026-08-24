package com.orderflow.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChargeNotFoundException extends RuntimeException {
    public ChargeNotFoundException(String chargeId) {
        super("Payment not found for this charge Id : " + chargeId);
    }
}
