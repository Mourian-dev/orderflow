package com.orderflow.order.kafka;

import com.orderflow.order.event.PaymentRefundedEvent;
import com.orderflow.order.event.StockReleasedEvent;
import com.orderflow.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CancellationCompletionConsumer {
    private final OrderService orderService;

    @KafkaListener(topics = "stock.released", groupId = "order-service")
    public void onStockReleased(StockReleasedEvent event, Acknowledgment ack) {
        orderService.markStockReleased(event.orderId());
        ack.acknowledge();
    }

    @KafkaListener(topics = "payment.refunded", groupId = "order-service")
    public void onPaymentRefunded(PaymentRefundedEvent event, Acknowledgment ack) {
        orderService.markPaymentRefunded(event.orderId());
        ack.acknowledge();
    }
}
