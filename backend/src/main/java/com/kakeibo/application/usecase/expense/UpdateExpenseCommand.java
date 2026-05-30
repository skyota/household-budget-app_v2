package com.kakeibo.application.usecase.expense;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateExpenseCommand(
    Long expenseId,
    String title,
    long price,
    LocalDate expenseDate,
    Long categoryId,
    String memo,
    UUID userId
) {}
