package com.kakeibo.application.usecase.investsetting;

import java.util.UUID;

public record UpdateInvestSettingCommand(
    long amount,
    int investDate,
    UUID userId
) {}
