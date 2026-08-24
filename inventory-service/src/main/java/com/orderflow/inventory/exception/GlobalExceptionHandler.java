package com.orderflow.inventory.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SkuNotFoundException.class)
    public ProblemDetail handleSkuNotFound(SkuNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("SKU Not Found");
        problem.setType(URI.create("https://orderflow.internal/errors/sku-not-found"));
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Insufficient Stock");
        problem.setType(URI.create("https://orderflow.internal/errors/insufficient-stock"));
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("sku", ex.getSku());
        problem.setProperty("requestedQty", ex.getRequestedQty());
        problem.setProperty("availableQty", ex.getAvailableQty());
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
