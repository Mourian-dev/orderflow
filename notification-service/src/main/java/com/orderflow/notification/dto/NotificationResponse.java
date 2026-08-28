package com.orderflow.notification.dto;

import com.orderflow.notification.entity.CancellationNotification;

public record NotificationResponse(Long orderId,boolean stockReleased,boolean paymentRefunded,boolean notified) {

    public static NotificationResponse from(CancellationNotification notification) {
        return new NotificationResponse(
                notification.getOrderId(),
                Boolean.TRUE.equals(notification.getStockReleased()),
                Boolean.TRUE.equals(notification.getPaymentRefunded()),
                notification.getNotifiedAt() != null);
    }
}
