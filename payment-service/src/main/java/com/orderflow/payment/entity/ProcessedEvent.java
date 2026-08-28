package com.orderflow.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "processed_events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProcessedEvent {
    @Id
    private Long orderId;

    private Instant processedAt;
}
