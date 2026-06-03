package com.kakeibo.application.usecase.cashbalance;

import java.time.LocalDate;
import java.util.UUID;

import com.kakeibo.domain.model.cashbalance.BalanceType;

public record UpdateCashBalanceCommand(
    Long cashBalanceId,
    long amount,
    LocalDate balanceDate,
    BalanceType balanceType,
    String memo,
    UUID userId
) {}
