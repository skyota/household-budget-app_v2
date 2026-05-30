package com.kakeibo.presentation.dto;

import java.time.LocalDate;

public record ExpenseRequest(
    String title,
    Long price,
    LocalDate expenseDate,
    Long categoryId, // nullable
    String memo // nullable
) {}
