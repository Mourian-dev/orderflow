package com.orderflow.notification.kafka;

import com.orderflow.notification.event.PaymentRefundedEvent;
import com.orderflow.notification.event.StockReleasedEvent;
import com.orderflow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCancellationConsumer {
    private final NotificationService service;

    @KafkaListener(topics = "stock.released", groupId = "notification-service")
    public void onStockReleased(StockReleasedEvent event, Acknowledgment ack) {
        service.onStockReleased(event.orderId());
        ack.acknowledge();
    }

    @KafkaListener(topics = "payment.refunded", groupId = "notification-service")
    public void onPaymentRefunded(PaymentRefundedEvent event, Acknowledgment ack) {
        service.onPaymentRefunded(event.orderId());
        ack.acknowledge();
    }
}
