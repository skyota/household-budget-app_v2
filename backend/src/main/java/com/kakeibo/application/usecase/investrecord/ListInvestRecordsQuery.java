package com.kakeibo.application.usecase.investrecord;

import java.util.UUID;

public record ListInvestRecordsQuery(
    UUID userId,
    int page,
    int perPage
) {}
