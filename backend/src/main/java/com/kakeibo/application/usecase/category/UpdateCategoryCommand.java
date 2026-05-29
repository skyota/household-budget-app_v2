package com.kakeibo.application.usecase.category;

import java.util.UUID;

public record UpdateCategoryCommand(
    Long categoryId,
    String name,
    long budgetAmount,
    UUID userId
) {}
