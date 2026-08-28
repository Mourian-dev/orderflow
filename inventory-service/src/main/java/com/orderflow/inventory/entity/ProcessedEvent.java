package com.orderflow.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("processed_events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProcessedEvent {

    @PrimaryKey
    private Long orderId;

    @Column("processed_at")
    private Instant processedAt;
}
