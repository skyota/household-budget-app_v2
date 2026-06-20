package com.kakeibo.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.invest.InvestRecord;
import com.kakeibo.domain.model.invest.InvestType;

public interface InvestRecordRepository {
    Optional<InvestRecord> findByIdAndUserId(Long id, UUID userId);
    List<InvestRecord> findAllByUserId(UUID userId);
    boolean existsByUserIdAndInvestTypeAndInvestDateBetween(UUID userId, InvestType investType, LocalDate startDate, LocalDate endDate);
    InvestRecord save(InvestRecord investRecord);
    void delete(InvestRecord investRecord);
}
