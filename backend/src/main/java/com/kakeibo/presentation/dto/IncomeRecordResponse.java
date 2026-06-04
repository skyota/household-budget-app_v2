package com.kakeibo.presentation.dto;

import java.time.LocalDate;

public record IncomeRecordResponse(
    Long id,
    String title,
    long amount,
    LocalDate incomeDate,
    String memo,
    boolean isRegular
) {}
