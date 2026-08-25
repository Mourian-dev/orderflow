package com.orderflow.payment.controller;

import com.orderflow.payment.dto.ChargeRequest;
import com.orderflow.payment.dto.ChargeResponse;
import com.orderflow.payment.dto.ReverseRequest;
import com.orderflow.payment.dto.ReverseResponse;
import com.orderflow.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;

    @PreAuthorize("hasAuthority('PAYMENT_WRITE')")
    @PostMapping("/charge")
    public ChargeResponse charge(@Valid @RequestBody ChargeRequest request) {
        return service.charge(request);
    }

    @PreAuthorize("hasAuthority('PAYMENT_WRITE')")
    @PostMapping("/reverse")
    public ReverseResponse reverse(@Valid @RequestBody ReverseRequest request) {
        return service.reverse(request);
    }
}
