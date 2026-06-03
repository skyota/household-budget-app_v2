package com.kakeibo.application.usecase.fixedexpense;

import java.util.UUID;

public record ListFixedExpensesQuery(
    UUID userId,
    int page,
    int perPage
) {}
