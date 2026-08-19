package com.orderflow.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain) {
        ServerHttpRequest incomingRequest = exchange.getRequest();

        String correlationId = incomingRequest.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if(correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        ServerHttpRequest outgoingRequest = incomingRequest.mutate()
                .header(CORRELATION_ID_HEADER, correlationId).build();

        ServerWebExchange mutatedExchange= exchange.mutate().request(outgoingRequest).build();
        mutatedExchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, correlationId);

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
