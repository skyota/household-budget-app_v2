package com.kakeibo.application.usecase.expense;

import java.time.LocalDate;

public record ExpenseItemResult(
    Long id,
    String title,
    long price,
    LocalDate expenseDate,
    Long categoryId,
    String categoryName,
    String memo
) {}
