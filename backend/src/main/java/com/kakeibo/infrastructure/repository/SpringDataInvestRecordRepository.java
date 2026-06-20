package com.kakeibo.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakeibo.domain.model.invest.InvestType;
import com.kakeibo.infrastructure.entity.InvestRecordEntity;

public interface SpringDataInvestRecordRepository extends JpaRepository<InvestRecordEntity, Long> {
    Optional<InvestRecordEntity> findByIdAndUserId(Long id, UUID userId);
    List<InvestRecordEntity> findAllByUserId(UUID userId);
    boolean existsByUserIdAndInvestTypeAndInvestDateBetween(UUID userId, InvestType investType, LocalDate startDate, LocalDate endDate);
}
