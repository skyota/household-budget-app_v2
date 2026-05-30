package com.kakeibo.application.usecase.expense;

import java.util.UUID;

public record ListExpensesQuery(
    UUID userId,
    int page,
    int perPage
) {}
