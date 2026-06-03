package com.kakeibo.application.usecase.cashbalance;

import java.time.LocalDate;

import com.kakeibo.domain.model.cashbalance.BalanceType;
import com.kakeibo.domain.model.cashbalance.CashBalance;

public record CashBalanceResult(
    Long id,
    long amount,
    LocalDate balanceDate,
    BalanceType balanceType,
    String memo
) {
    public static CashBalanceResult from(CashBalance cashBalance) {
        return new CashBalanceResult(
            cashBalance.getId(),
            cashBalance.getAmount(),
            cashBalance.getBalanceDate(),
            cashBalance.getBalanceType(),
            cashBalance.getMemo()
        );
    }
}
