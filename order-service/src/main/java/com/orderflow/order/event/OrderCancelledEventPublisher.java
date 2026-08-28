package com.orderflow.order.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCancelledEventPublisher {
    public static final String TOPIC = "order.cancelled";

    private final KafkaTemplate<String, OrderCancelledEvent> kafkaTemplate;

    public void publish(OrderCancelledEvent event) {
        kafkaTemplate.send(TOPIC, event.orderId().toString(), event);
    }
}
