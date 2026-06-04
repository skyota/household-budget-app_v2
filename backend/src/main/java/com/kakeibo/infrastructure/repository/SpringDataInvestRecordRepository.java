package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakeibo.infrastructure.entity.InvestRecordEntity;

public interface SpringDataInvestRecordRepository extends JpaRepository<InvestRecordEntity, Long> {
    Optional<InvestRecordEntity> findByIdAndUserId(Long id, UUID userId);
    List<InvestRecordEntity> findAllByUserId(UUID userId);
}
