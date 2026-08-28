package com.orderflow.payment.kafka;

import com.orderflow.payment.event.OrderCancelledEvent;
import com.orderflow.payment.event.PaymentRefundedEvent;
import com.orderflow.payment.service.OrderCancellationHandler;
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
    public static final String PAYMENT_REFUNDED_TOPIC = "payment.refunded";

    private final OrderCancellationHandler handler;
    private final KafkaTemplate<String, PaymentRefundedEvent> kafkaTemplate;

    @KafkaListener(topics = "order.cancelled", groupId = "payment-service")
    public void onOrderCancelled(OrderCancelledEvent event, Acknowledgment ack) {
        handler.handle(event);

        kafkaTemplate.send(PAYMENT_REFUNDED_TOPIC, event.orderId().toString(), new PaymentRefundedEvent(event.orderId()));
        ack.acknowledge();;
    }
}
