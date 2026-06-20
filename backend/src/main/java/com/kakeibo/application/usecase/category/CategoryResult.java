package com.kakeibo.application.usecase.category;

public record CategoryResult(
    Long id,
    String name,
    long budgetAmount,
    boolean isSystem
) {}
