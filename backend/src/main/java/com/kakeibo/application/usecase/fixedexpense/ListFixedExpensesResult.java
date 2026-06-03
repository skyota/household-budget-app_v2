package com.kakeibo.application.usecase.fixedexpense;

import java.util.List;

import com.kakeibo.application.usecase.shared.PaginationResult;

public record ListFixedExpensesResult(
    List<FixedExpenseResult> data,
    PaginationResult pagination
) {}
