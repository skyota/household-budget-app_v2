package com.kakeibo.application.usecase.investsetting;

import java.util.UUID;

public record CreateInvestSettingCommand(
    long amount,
    int investDate,
    UUID userId
) {}
