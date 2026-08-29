package com.orderflow.fraudcheck.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "fraud_check_results")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FraudCheckResult {
    @Id
    private Long orderId;

    private String customerId;

    @Enumerated(EnumType.STRING)
    private FraudDecision decision;

    private double similarityScore;

    @Column(columnDefinition = "TEXT")
    private String matchedPattern;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    private BigDecimal amount;

    private Instant evaluatedAt;
}
