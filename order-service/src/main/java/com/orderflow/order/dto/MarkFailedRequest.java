package com.orderflow.order.dto;

import jakarta.validation.constraints.NotBlank;

public record MarkFailedRequest(@NotBlank(message = "Reason is required") String reason) { }
