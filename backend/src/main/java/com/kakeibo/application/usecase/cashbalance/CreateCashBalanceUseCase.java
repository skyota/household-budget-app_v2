package com.kakeibo.application.usecase.cashbalance;

public interface CreateCashBalanceUseCase {
    CashBalanceResult create(CreateCashBalanceCommand command);
}
