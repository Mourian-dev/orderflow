package com.orderflow.notification.exception;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(Long orderId) {
        super("No cancellation notification for order: " + orderId);
    }
}
