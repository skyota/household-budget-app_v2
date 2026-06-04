package com.kakeibo.application.usecase.incomerecord;

import java.time.LocalDate;
import java.util.UUID;

public record CreateIncomeRecordCommand(
    String title,
    long amount,
    LocalDate incomeDate,
    String memo,
    boolean isRegular,
    UUID userId
) {}
