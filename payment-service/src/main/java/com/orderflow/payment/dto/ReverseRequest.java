package com.orderflow.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record ReverseRequest(
        @NotBlank(message = "Charge Id is required") String chargeId
) {
}
