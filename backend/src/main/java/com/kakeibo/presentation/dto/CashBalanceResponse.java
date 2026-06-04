package com.kakeibo.presentation.dto;

import java.time.LocalDate;

import com.kakeibo.domain.model.cashbalance.BalanceType;

public record CashBalanceResponse(
    Long id,
    long amount,
    LocalDate balanceDate,
    BalanceType balanceType,
    String memo
) {}
