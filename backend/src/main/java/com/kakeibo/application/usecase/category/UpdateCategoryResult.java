package com.kakeibo.application.usecase.category;

public record UpdateCategoryResult(
    Long id,
    String name,
    Long budgetAmount
) {}
