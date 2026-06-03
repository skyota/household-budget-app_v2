package com.kakeibo.application.usecase.cashbalance;

import java.util.List;

import com.kakeibo.application.usecase.shared.PaginationResult;

public record ListCashBalanceResult(
    List<CashBalanceResult> data,
    PaginationResult pagination
) {}
