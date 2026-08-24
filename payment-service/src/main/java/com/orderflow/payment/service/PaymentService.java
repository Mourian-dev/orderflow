package com.orderflow.payment.service;

import com.orderflow.payment.dto.ChargeRequest;
import com.orderflow.payment.dto.ChargeResponse;
import com.orderflow.payment.dto.ReverseRequest;
import com.orderflow.payment.dto.ReverseResponse;
import com.orderflow.payment.entity.Payment;
import com.orderflow.payment.exception.ChargeNotFoundException;
import com.orderflow.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;

    public ChargeResponse charge(ChargeRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.orderId());
        payment.setAmount(request.amount());
        payment.setChargeId(UUID.randomUUID().toString());
        payment.setStatus("CHARGED");
        repository.save(payment);
        return ChargeResponse.from(payment);
    }

    public ReverseResponse reverse(ReverseRequest request) {
        Payment chargedPayment = repository.findByChargeId(request.chargeId()).orElseThrow(() -> new ChargeNotFoundException(request.chargeId()));
        chargedPayment.setStatus("REVERSED");
        repository.save(chargedPayment);
        return ReverseResponse.from(chargedPayment);
    }
}
