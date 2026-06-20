package com.kakeibo.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.income.IncomeRecord;

public interface IncomeRecordRepository {
    Optional<IncomeRecord> findByIdAndUserId(Long id, UUID userId);
    List<IncomeRecord> findAllByUserId(UUID userId);
    boolean existsByUserIdAndIsRegularAndTitleAndIncomeDateBetween(UUID userId, boolean isRegular, String title, LocalDate startDate, LocalDate endDate);
    IncomeRecord save(IncomeRecord incomeRecord);
    void delete(IncomeRecord incomeRecord);
}
