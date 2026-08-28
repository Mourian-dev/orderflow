package com.orderflow.notification.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "Cancellation_notifications")
@Getter
@Setter
@NoArgsConstructor
public class CancellationNotification {

    @Id
    private Long orderId;

    private Boolean stockReleased;

    private Boolean paymentRefunded;

    private Instant notifiedAt;

    public CancellationNotification(Long orderId) {
        this.orderId = orderId;
        this.stockReleased = false;
        this.paymentRefunded = false;
    }
}
