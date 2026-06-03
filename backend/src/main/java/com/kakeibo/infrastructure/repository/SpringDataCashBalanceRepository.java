package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakeibo.infrastructure.entity.CashBalanceEntity;

public interface SpringDataCashBalanceRepository extends JpaRepository<CashBalanceEntity, Long> {
    Optional<CashBalanceEntity> findByIdAndUserId(Long id, UUID userId);
    List<CashBalanceEntity> findAllByUserId(UUID userId);
    Optional<CashBalanceEntity> findTopByUserIdOrderByBalanceDateDesc(UUID userId);
}
