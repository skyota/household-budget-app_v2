package com.kakeibo.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.expense.Expense;

public interface ExpenseRepository {
    Optional<Expense> findByIdAndUserId(Long expenseId, UUID userId);
    List<Expense> findAllByUserId(UUID userId);
    Expense save(Expense expense);
    void delete(Expense expense);
}
