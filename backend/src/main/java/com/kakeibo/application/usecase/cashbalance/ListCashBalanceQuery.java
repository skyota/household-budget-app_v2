package com.kakeibo.application.usecase.cashbalance;

import java.util.UUID;

public record ListCashBalanceQuery(
    UUID userId,
    int page,
    int perPage
) {}
