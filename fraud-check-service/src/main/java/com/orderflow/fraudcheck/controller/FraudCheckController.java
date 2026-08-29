package com.orderflow.fraudcheck.controller;

import com.orderflow.fraudcheck.service.FraudCheckService;
import com.orderflow.fraudcheck.dto.FraudCheckRequest;
import com.orderflow.fraudcheck.dto.FraudCheckResponse;
import com.orderflow.fraudcheck.entity.FraudCheckResult;
import com.orderflow.fraudcheck.exception.FraudCheckResultNotFoundException;
import com.orderflow.fraudcheck.repository.FraudCheckResultRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/fraud-check")
public class FraudCheckController {
    private final FraudCheckService fraudCheckService;
    private final FraudCheckResultRepository repository;

    @PostMapping("/evaluate")
    public FraudCheckResponse evaluate(@Valid @RequestBody FraudCheckRequest request) {
        return fraudCheckService.evaluate(request);
    }

    // Not called by SagaOrchestrator at all -- exists purely so this stage's
    // own "Verify It" can pull the persisted score/reasoning back out directly,
    // the identical role Notification Service's GET /notifications/{orderId}
    // played in Stage 10.
    @GetMapping("/{orderId}")
    public FraudCheckResponse get(@PathVariable Long orderId) {
        FraudCheckResult result = repository.findById(orderId)
                .orElseThrow(() -> new FraudCheckResultNotFoundException(orderId));
        return new FraudCheckResponse(result.getOrderId(), result.getDecision().name(),
                result.getSimilarityScore(), result.getMatchedPattern(), result.getReasoning());
    }
}
