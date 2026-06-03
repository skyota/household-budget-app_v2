package com.kakeibo.application.usecase.incomesetting;

import java.util.UUID;

public record ListIncomeSettingsQuery(
    UUID userId,
    int page,
    int perPage
) {}
