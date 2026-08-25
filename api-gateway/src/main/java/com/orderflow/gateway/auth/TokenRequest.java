package com.orderflow.gateway.auth;

public record TokenRequest(String customerId, String role) { }
