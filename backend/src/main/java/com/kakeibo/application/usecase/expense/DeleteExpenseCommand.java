package com.kakeibo.application.usecase.expense;

import java.util.UUID;

public record DeleteExpenseCommand(
    Long expenseId,
    UUID userId
) {}
