package com.orderflow.fraudcheck.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class FraudPatternSeeder implements CommandLineRunner {
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        Integer existing = jdbcTemplate
                .queryForObject("SELECT COUNT(*) FROM fraud_patterns", Integer.class);

        if (existing != null && existing > 0) {
            log.info("fraud_patterns already seeded ({} rows) -- skipping", existing);
            return;
        }

        List<Document> knownPatterns = List.of(
                Document.builder()
                        .text("Large first-time order shipped to a different country than the billing address, " +
                                "paid with the card added to the account minutes later")
                        .metadata(Map.of("patternId", "P1"))
                        .build(),
                Document.builder()
                        .text("Several rapid back-to-back orders for the same high-value SKU from " +
                                "customer within few minutes")
                        .metadata(Map.of("pattern", "P2"))
                        .build(),
                Document.builder()
                        .text("An order amount far above that customer's historical avaerage combined with " +
                                "expedited shipping to freshly created address")
                        .build()
        );

        vectorStore.add(knownPatterns);
        log.info("Seeded {} known-fraud-pattern embeddings into fraud_patterns", knownPatterns.size());
    }
}
