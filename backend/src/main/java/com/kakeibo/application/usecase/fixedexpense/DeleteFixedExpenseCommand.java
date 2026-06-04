package com.kakeibo.application.usecase.fixedexpense;

import java.util.UUID;

public record DeleteFixedExpenseCommand(
    Long fixedExpenseId,
    UUID userId
) {}
