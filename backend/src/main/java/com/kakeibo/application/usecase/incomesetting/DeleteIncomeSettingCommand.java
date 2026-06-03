package com.kakeibo.application.usecase.incomesetting;

import java.util.UUID;

public record DeleteIncomeSettingCommand(
    Long incomeSettingId,
    UUID userId
) {}
