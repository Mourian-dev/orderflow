package com.orderflow.inventory.dedup;

import com.orderflow.inventory.entity.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.EntityWriteResult;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class EventDedupGuard {
    private final CassandraTemplate cassandraTemplate;

    public boolean claim(Long orderId) {
        ProcessedEvent event = new ProcessedEvent(orderId, Instant.now());
        EntityWriteResult<ProcessedEvent> result = cassandraTemplate.insert(event, InsertOptions.builder().withIfNotExists().build());
        return result.wasApplied();
    }
}
