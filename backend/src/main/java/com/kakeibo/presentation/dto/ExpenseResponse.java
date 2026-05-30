package com.kakeibo.presentation.dto;

import java.time.LocalDate;

public record ExpenseResponse(
    Long id,
    String title,
    long price,
    LocalDate expenseDate,
    Long categoryId,
    String memo
) {}
