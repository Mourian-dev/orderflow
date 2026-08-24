package com.orderflow.saga.client.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class InventoryClient {
    public static final String RESERVE_URL = "http://inventory-service/inventory/reserve";
    public static final String RELEASE_URL = "http://inventory-service/inventory/release";

    private final RestTemplate restTemplate;

    public InventoryReserveResponse reserve(String sku, int qty) {
        return restTemplate.postForObject(RESERVE_URL, new InventoryReserveRequest(sku, qty), InventoryReserveResponse.class);
    }

    public void release(String sku, int qty) {
        restTemplate.postForObject(RELEASE_URL, new InventoryReleaseRequest(sku, qty), InventoryReleaseResponse.class);
    }
}
