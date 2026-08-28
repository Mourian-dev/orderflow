package com.orderflow.inventory.kafka;

import com.orderflow.inventory.dedup.EventDedupGuard;
import com.orderflow.inventory.dto.ReleaseRequest;
import com.orderflow.inventory.event.OrderCancelledEvent;
import com.orderflow.inventory.event.StockReleasedEvent;
import com.orderflow.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCancelledConsumer {

    public static final String STOCK_RELEASED_TOPIC = "stock.released";

    private final InventoryService inventoryService;
    private final EventDedupGuard dedupGuard;
    private final KafkaTemplate<String, StockReleasedEvent> kafkaTemplate;

    @KafkaListener(topics = "order.cancelled", groupId = "inventory-service")
    public void onOrderCancelled(OrderCancelledEvent event, Acknowledgment ack) {
        if(dedupGuard.claim(event.orderId())) {
            inventoryService.release(new ReleaseRequest(event.sku(), event.qty()));
            log.info("Released {} units of {} for cancelled order {}", event.qty(), event.sku(), event.orderId());
        } else {
            log.info("order.cancelled for order {} already processed by inventory-service -- skipping release", event.orderId());
        }

        kafkaTemplate.send(STOCK_RELEASED_TOPIC, event.orderId().toString(), new StockReleasedEvent(event.orderId()));
        ack.acknowledge();
    }
}
