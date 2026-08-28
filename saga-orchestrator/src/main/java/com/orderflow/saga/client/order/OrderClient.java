package com.orderflow.saga.client.order;

import com.orderflow.saga.client.fraudCheck.MarkFailedRequest;
import com.orderflow.saga.dto.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OrderClient {
    public static final String ORDERS_URL = "http://order-service/orders";

    private final RestTemplate restTemplate;

    public OrderResponse create(CreateOrderCommand cmd) {
        CreateOrderRequest request = new CreateOrderRequest(cmd.sku(), cmd.qty(), cmd.customerId(), cmd.amount());
        return restTemplate.postForObject(ORDERS_URL, request, OrderResponse.class);
    }

    public OrderResponse confirm(Long orderId, String chargeId) {
        HttpEntity<ConfirmRequest> requestEntity = new HttpEntity<>(new ConfirmRequest(chargeId));
        return restTemplate.exchange(ORDERS_URL + "/" + orderId + "/confirm", HttpMethod.PUT, requestEntity, OrderResponse.class).getBody();
    }

    public OrderResponse markFailed(Long orderId, String reason) {
        HttpEntity<MarkFailedRequest> requestHttpEntity = new HttpEntity<>(new MarkFailedRequest(reason));
        return restTemplate.exchange(ORDERS_URL + "/" + orderId + "/fail", HttpMethod.PUT, requestHttpEntity, OrderResponse.class).getBody();
    }
}
