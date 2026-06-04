package com.kakeibo.presentation.dto;

public record FixedExpenseResponse(
    Long id,
    String title,
    long price,
    int fixedExpenseDate,
    String memo
) {}
