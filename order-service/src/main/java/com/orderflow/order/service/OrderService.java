package com.orderflow.order.service;

import com.orderflow.order.client.InventoryReserveRequest;
import com.orderflow.order.client.InventoryReserveResponse;
import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.entity.Order;
import com.orderflow.order.repository.OrderRepository;
import com.orderflow.order.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    public static final String INVENTORY_RESERVE_URL = "http://inventory-service/inventory/reserve";

    private final OrderRepository repository;
    private final RestTemplate restTemplate;

    public Order createOrder(CreateOrderRequest request) {
        InventoryReserveRequest reserveRequest = new InventoryReserveRequest(request.sku(), request.quantity());
        InventoryReserveResponse reserveResponse = restTemplate
                .postForObject(INVENTORY_RESERVE_URL, reserveRequest, InventoryReserveResponse.class);

        log.info("Inventory reserved:: sku = {} reservedQty = {} availableQty = {}",
                request.sku(), reserveResponse.reservedQty(), reserveResponse.availableQty());

        Order order = new Order();
        order.setSku(request.sku());
        order.setAmount(request.amount());
        order.setQuantity(request.quantity());
        order.setCustomerId(request.customerId());
        order.setStatus("PENDING");
        return repository.save(order);
    }

    public Order getOrder(Long orderId) {
        return repository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public List<Order> list() {
        return repository.findAll();
    }

    public void delete(Long orderId) {
        if(repository.existsById(orderId))
            repository.deleteById(orderId);
        else throw new OrderNotFoundException(orderId);
    }
}
