package com.kakeibo.presentation.dto;

public record IncomeSettingResponse(
    Long id,
    String title,
    long amount,
    int incomeDate,
    String memo,
    boolean isAutoGenerate
) {}
