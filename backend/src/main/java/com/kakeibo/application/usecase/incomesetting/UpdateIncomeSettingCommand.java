package com.kakeibo.application.usecase.incomesetting;

import java.util.UUID;

public record UpdateIncomeSettingCommand(
    Long incomeSettingId,
    String title,
    long amount,
    int incomeDate,
    String memo,
    boolean isAutoGenerate,
    UUID userId
) {}
