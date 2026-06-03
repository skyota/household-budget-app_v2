package com.kakeibo.application.usecase.cashbalance;

import java.util.UUID;

public record DeleteCashBalanceCommand(
    Long cashBalanceId,
    UUID userId
) {}
