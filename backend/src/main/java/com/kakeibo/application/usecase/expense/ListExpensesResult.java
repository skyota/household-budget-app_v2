package com.kakeibo.application.usecase.expense;

import java.util.List;

import com.kakeibo.application.usecase.shared.PaginationResult;

public record ListExpensesResult(
    List<ExpenseItemResult> expenses,
    PaginationResult pagination
) {}
