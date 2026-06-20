package com.kakeibo.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.expense.Expense;

public interface ExpenseRepository {
    Optional<Expense> findByIdAndUserId(Long expenseId, UUID userId);
    List<Expense> findAllByUserId(UUID userId);
    boolean existsByFixedExpenseIdAndExpenseDateBetween(Long fixedExpenseId, LocalDate startDate, LocalDate endDate);
    Expense save(Expense expense);
    void delete(Expense expense);
}
