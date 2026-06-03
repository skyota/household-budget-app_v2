package com.kakeibo.application.usecase.incomesetting;

import java.util.UUID;

public record CreateIncomeSettingCommand(
    String title,
    long amount,
    int incomeDate,
    String memo,
    boolean isAutoGenerate,
    UUID userId
) {}
