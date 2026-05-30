package com.kakeibo.application.usecase.expense;

import java.util.List;

public record ListExpensesResult(
    List<ExpenseItemResult> expenses,
    PaginationResult pagination
) {}
