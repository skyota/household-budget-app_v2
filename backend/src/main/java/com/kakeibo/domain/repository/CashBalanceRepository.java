package com.kakeibo.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.cashbalance.CashBalance;

public interface CashBalanceRepository {
    Optional<CashBalance> findByIdAndUserId(Long id, UUID userId);
    Optional<CashBalance> findLatestByUserId(UUID userId);
    List<CashBalance> findAllByUserId(UUID userId);
    CashBalance save(CashBalance cashBalance);
    void delete(CashBalance cashBalance);
}
