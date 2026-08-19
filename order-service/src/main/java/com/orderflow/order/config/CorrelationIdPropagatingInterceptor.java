package com.orderflow.order.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

public class CorrelationIdPropagatingInterceptor implements ClientHttpRequestInterceptor {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes != null) {
            String correlationId = attributes.getRequest().getHeader(CORRELATION_ID_HEADER);

            if(correlationId != null && !request.getHeaders().containsKey(CORRELATION_ID_HEADER)) {
                request.getHeaders().add(CORRELATION_ID_HEADER, correlationId);
            }
        }

        return execution.execute(request, body);
    }
}
