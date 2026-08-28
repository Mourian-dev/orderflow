package com.orderflow.order.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmRequest(@NotBlank(message = "chargeId is required") String chargeId) { }
