package com.orderflow.saga.client.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentClient {
    public static final String CHARGE_URL = "http://payment-service/payments/charge";
    public static final String REVERSE_URL = "http://payment-service/payments/reverse";

    private final RestTemplate restTemplate;

    public PaymentChargeResponse charge(Long orderId, BigDecimal amount) {
        return restTemplate.postForObject(CHARGE_URL, new PaymentChargeRequest(orderId, amount), PaymentChargeResponse.class);
    }

    public void reverse(String chargeId) {
        restTemplate.postForObject(REVERSE_URL, new PaymentReverseRequest(chargeId), PaymentReverseResponse.class);
    }
}
