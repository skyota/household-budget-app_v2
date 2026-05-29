package com.kakeibo.application.usecase.category;

public record CreateCategoryResult(
    Long id,
    String name,
    Long budgetAmount
) {}
