package com.orderflow.notification.service;

import com.orderflow.notification.entity.CancellationNotification;
import com.orderflow.notification.exception.NotificationNotFoundException;
import com.orderflow.notification.repository.CancellationNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final CancellationNotificationRepository repository;

    @Transactional
    public void onStockReleased(Long orderId) {
        CancellationNotification notification = repository.findById(orderId)
                .orElseGet(() -> new CancellationNotification(orderId));
        notification.setStockReleased(true);
        maybeNotify(notification);
        repository.save(notification);
    }

    @Transactional
    public void onPaymentRefunded(Long orderId) {
        CancellationNotification notification = repository.findById(orderId)
                .orElseGet(() -> new CancellationNotification(orderId));
        notification.setPaymentRefunded(true);
        maybeNotify(notification);
        repository.save(notification);
    }

    private void maybeNotify(CancellationNotification notification) {
        if(Boolean.TRUE.equals(notification.getStockReleased()) && Boolean.TRUE.equals(notification.getPaymentRefunded()) && notification.getNotifiedAt() == null) {
            notification.setNotifiedAt(Instant.now());
            log.info("[MOCK EMAIL] Order {} cancellation confirmed -- stock released and payment refunded.", notification.getOrderId());
        }
    }

    public CancellationNotification get(Long orderId) {
        return repository.findById(orderId).orElseThrow(() -> new NotificationNotFoundException(orderId));
    }
}
