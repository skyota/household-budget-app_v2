package com.kakeibo.application.usecase.expense;

import java.time.LocalDate;

public record UpdateExpenseResult(
    Long id,
    String title,
    long price,
    LocalDate expenseDate,
    Long categoryId,
    String memo
) {}
