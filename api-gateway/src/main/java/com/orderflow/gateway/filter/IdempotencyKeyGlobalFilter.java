package com.orderflow.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class IdempotencyKeyGlobalFilter implements GlobalFilter, Ordered {
    public static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    public static final String REPLAY_HEADER = "X-Idempotency-Replayed";
    public static final String REDIS_KEY_PREFIX = "idempotency:";

    public static final Duration TTL = Duration.ofHours(24);

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean isCreateOrder = HttpMethod.POST.equals(exchange.getRequest().getMethod())
                && "/orders".equals(exchange.getRequest().getPath().value());
        String idempotencyKey = exchange.getRequest().getHeaders().getFirst(IDEMPOTENCY_KEY_HEADER);

        if(!isCreateOrder || idempotencyKey == null || idempotencyKey.isBlank()) {
            return chain.filter(exchange);
        }

        String redisKey = REDIS_KEY_PREFIX + idempotencyKey;

        return redisTemplate.opsForValue().get(redisKey)
                .flatMap(cachedJson -> replay(exchange, cachedJson))
                .switchIfEmpty(Mono.defer(() -> forwardAndCache(exchange, chain, redisKey)))
                .onErrorResume(JsonProcessingException.class, e -> forwardAndCache(exchange, chain, redisKey));
    }

    private Mono<Void> replay(ServerWebExchange exchange, String cachedJson) {
        CachedResponse cached;

        try {
            cached = objectMapper.readValue(cachedJson, CachedResponse.class);
        } catch(JsonProcessingException e) {
            return Mono.error(e);
        }

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatusCode.valueOf(cached.status()));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().set(REPLAY_HEADER, "true");

        DataBuffer buffer = response.bufferFactory().wrap(cached.body().getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> forwardAndCache(ServerWebExchange exchange, GatewayFilterChain chain, String redisKey) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpStatusCode statusCode = getStatusCode();
                if(statusCode == null || !statusCode.is2xxSuccessful() || !(body instanceof Flux)) {
                    return super.writeWith(body);
                }

                Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                return super.writeWith(fluxBody.collectList().flatMap(dataBuffers -> {
                    DataBuffer joined = bufferFactory.join(dataBuffers);
                    byte[] content = new byte[joined.readableByteCount()];
                    joined.read(content);
                    DataBufferUtils.release(joined);

                    String responseBody = new String(content, StandardCharsets.UTF_8);
                    CachedResponse cached = new CachedResponse(statusCode.value(), responseBody);

                    Mono<Boolean> cacheWrite;

                    try {
                        String json = objectMapper.writeValueAsString(cached);
                        cacheWrite = redisTemplate.opsForValue().set(redisKey, json, TTL);
                    } catch(JsonProcessingException e) {
                        cacheWrite = Mono.just(false);
                    }

                    return cacheWrite.thenReturn(bufferFactory.wrap(content));
                }).flux());
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    private record CachedResponse(int status,String body) {}
}
