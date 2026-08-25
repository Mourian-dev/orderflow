package com.orderflow.saga.client.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class PaymentClient {
    public static final String CHARGE_URL = "http://payment-service/payments/charge";
    public static final String REVERSE_URL = "http://payment-service/payments/reverse";

    private final RestTemplate restTemplate;
    public final String paymentAuthToken;

    public PaymentClient(RestTemplate restTemplate, @Value("${saga.payment.auth-token}") String paymentAuthToken) {
        this.restTemplate = restTemplate;
        this.paymentAuthToken = paymentAuthToken;
    }

    public PaymentChargeResponse charge(Long orderId, BigDecimal amount) {
        HttpEntity<PaymentChargeRequest> requestHttpEntity = authorizedEntity(new PaymentChargeRequest(orderId, amount));
        return restTemplate.postForObject(CHARGE_URL, requestHttpEntity, PaymentChargeResponse.class);
    }

    public void reverse(String chargeId) {
        HttpEntity<PaymentReverseRequest> requestHttpEntity = authorizedEntity(new PaymentReverseRequest(chargeId));
        restTemplate.postForObject(REVERSE_URL, requestHttpEntity, PaymentReverseResponse.class);
    }

    public <T> HttpEntity<T> authorizedEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(paymentAuthToken);
        return new HttpEntity<>(body, headers);
    }
}
