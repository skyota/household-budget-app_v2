package com.kakeibo.application.usecase.incomerecord;

import java.util.UUID;

public record ListIncomeRecordsQuery(
    UUID userId,
    int page,
    int perPage
) {}
