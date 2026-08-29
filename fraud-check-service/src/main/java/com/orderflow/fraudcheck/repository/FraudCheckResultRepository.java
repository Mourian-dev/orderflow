package com.orderflow.fraudcheck.repository;

import com.orderflow.fraudcheck.entity.FraudCheckResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudCheckResultRepository extends JpaRepository<FraudCheckResult, Long> { }
