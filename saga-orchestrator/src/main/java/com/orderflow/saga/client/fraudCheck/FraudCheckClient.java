package com.orderflow.saga.client.fraudCheck;

import com.orderflow.saga.dto.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
@RequiredArgsConstructor
public class FraudCheckClient {

    public static final String EVALUATE_URL = "http://fraud-check-service/fraud-check/evaluate";

    public final RestTemplate restTemplate;

    public FraudCheckResponse evaluate(Long orderId, CreateOrderCommand cmd) {
        return restTemplate.postForObject(
                EVALUATE_URL,
                new FraudCheckRequest(orderId, cmd.customerId(), List.of(new FraudCheckCartItem(cmd.sku(), cmd.qty())), cmd.amount()),
                FraudCheckResponse.class
        );
    }
}
