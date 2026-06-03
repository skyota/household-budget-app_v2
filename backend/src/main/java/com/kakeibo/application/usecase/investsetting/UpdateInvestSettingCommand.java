package com.kakeibo.application.usecase.investsetting;

import java.util.UUID;

public record UpdateInvestSettingCommand(
    Long investSettingId,
    long amount,
    int investDate,
    UUID userId
) {}
