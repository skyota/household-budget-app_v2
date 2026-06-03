package com.kakeibo.application.usecase.fixedexpense;

import java.util.UUID;

public record UpdateFixedExpenseCommand(
    Long fixedExpenseId,
    String title,
    long price,
    int fixedExpenseDate,
    String memo,
    UUID userId
) {}
