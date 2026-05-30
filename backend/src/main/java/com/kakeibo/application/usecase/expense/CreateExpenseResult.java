package com.kakeibo.application.usecase.expense;

import java.time.LocalDate;

public record CreateExpenseResult(
    Long id,
    String title,
    long price,
    LocalDate expenseDate,
    Long categoryId,
    String memo
) {}
