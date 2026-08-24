package com.orderflow.order.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Order Not Found");;
        problem.setType(URI.create("https://orderflow.internal/errors/order-not-found"));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(RestClientException.class)
    public ProblemDetail handleDownStreamFailure(RestClientException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problem.setTitle("Downstream Service Failure");
        problem.setType(URI.create("https://orderflow.internal/errors/downstream-failure"));
        problem.setDetail("A downstream service call failed while processing this request.");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Unexpected Error");
        problem.setType(URI.create("https://orderflow.internal/errors/unexpected"));
        problem.setDetail("An unexpected error occurred while processing this request.");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
}
