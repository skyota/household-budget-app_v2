package com.kakeibo.presentation.dto;

import java.time.LocalDate;

public record ExpenseListItemResponse(
    Long id,
    String title,
    long price,
    LocalDate expenseDate,
    Long categoryId,
    String categoryName,
    String memo
) {}
