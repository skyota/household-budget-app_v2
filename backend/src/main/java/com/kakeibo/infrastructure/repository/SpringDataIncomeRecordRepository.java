package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakeibo.infrastructure.entity.IncomeRecordEntity;

public interface SpringDataIncomeRecordRepository extends JpaRepository<IncomeRecordEntity, Long> {
    Optional<IncomeRecordEntity> findByIdAndUserId(Long id, UUID userId);
    List<IncomeRecordEntity> findAllByUserId(UUID userId);
}
