package com.orderflow.saga.client.fraudCheck;

import com.orderflow.saga.dto.CreateOrderCommand;
import org.springframework.stereotype.Component;

@Component
public class FraudCheckClient {

    public FraudDecision evaluate(CreateOrderCommand cmd) {
        return FraudDecision.APPROVE;
    }
}
