package com.kakeibo.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.fixedexpense.FixedExpense;

public interface FixedExpenseRepository {
    Optional<FixedExpense> findByIdAndUserId(Long id, UUID userId);
    List<FixedExpense> findAllByUserId(UUID userId);
    List<FixedExpense> findAll();
    FixedExpense save(FixedExpense fixedExpense);
    void delete(FixedExpense fixedExpense);
}
