package com.kakeibo.application.usecase.cashbalance;

public interface UpdateCashBalanceUseCase {
    CashBalanceResult update(UpdateCashBalanceCommand command);
}
