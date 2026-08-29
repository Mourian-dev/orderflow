package com.orderflow.fraudcheck.service;

import com.orderflow.fraudcheck.dto.FraudCheckRequest;
import com.orderflow.fraudcheck.dto.FraudCheckResponse;
import com.orderflow.fraudcheck.entity.FraudCheckResult;
import com.orderflow.fraudcheck.entity.FraudDecision;
import com.orderflow.fraudcheck.repository.FraudCheckResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudCheckService {
    public static final BigDecimal HARD_DECLINE_AMOUNT = new BigDecimal("500.00");
    private static final double SIMILARITY_DECLINE_THRESHOLD = 0.82;

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final FraudCheckResultRepository repository;

    public FraudCheckResponse evaluate(FraudCheckRequest request) {
        String queryText = buildQueryText(request);
        List<Document> matches = vectorStore.similaritySearch(
                SearchRequest.builder().query(queryText).topK(1).build());
        double bestScore = matches.isEmpty() || matches.get(0).getScore() == null ? 0.0 : matches.get(0).getScore();
        String matchedPattern = matches.isEmpty() ? null : matches.get(0).getText();
        boolean amountTriggered = request.amount().compareTo(HARD_DECLINE_AMOUNT) >= 0;
        boolean similarityTriggered = bestScore >= SIMILARITY_DECLINE_THRESHOLD;
        FraudDecision decision = (amountTriggered || similarityTriggered) ? FraudDecision.DECLINE : FraudDecision.APPROVE;
        String reasoning = generateReasoning(request, decision, bestScore, matchedPattern, amountTriggered);
        FraudCheckResult result = new FraudCheckResult(request.orderId(), request.customerId(), decision,
                bestScore, matchedPattern, reasoning, request.amount(), Instant.now());
        repository.save(result);

        log.info("Fraud check for order {}: decision={} score={} amountTriggered={}",
                request.orderId(), decision, bestScore, amountTriggered);

        return new FraudCheckResponse(request.orderId(), decision.name(), bestScore, matchedPattern, reasoning);
    }

    private String buildQueryText(FraudCheckRequest request) {
        StringBuilder cartDescription = new StringBuilder();
        for (var item : request.cart()) {
            if (!cartDescription.isEmpty()) {
                cartDescription.append(", ");
            }
            cartDescription.append(item.qty()).append("x ").append(item.sku());
        }
        return "Order for customer " + request.customerId() + ": " + cartDescription
                + ", total amount $" + request.amount();
    }

    private String generateReasoning(FraudCheckRequest request, FraudDecision decision, double score,
                                     String matchedPattern,boolean amountTriggered) {
        String matchedPatternText = matchedPattern != null ? matchedPattern : "no close match found";
        String prompt = """
                You are writing a one- or two-sentence entry for an internal fraud-review
                audit log. The APPROVE/DECLINE decision has already been made by a
                deterministic rule -- your only job is to explain it in plain English, not
                to change it or second-guess it.

                Order id: %d
                Customer id: %s
                Order amount: $%s
                Decision already made: %s
                Amount at or above the $500 automatic-review threshold: %s
                Closest known fraud pattern found by embedding similarity search: "%s"
                Cosine similarity score against that pattern (0 = unrelated, 1 = identical): %.3f

                Write the audit-log entry now, one or two sentences, referencing the actual
                score and amount above.
                """.formatted(request.orderId(), request.customerId(), request.amount(), decision.name(),
                amountTriggered, matchedPatternText, score);

        return chatClient.prompt().user(prompt).call().content();
    }
}
