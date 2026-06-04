package com.kakeibo.application.usecase.fixedexpense;

import java.util.UUID;

public record CreateFixedExpenseCommand(
    String title,
    long price,
    int fixedExpenseDate,
    String memo,
    UUID userId
) {}
