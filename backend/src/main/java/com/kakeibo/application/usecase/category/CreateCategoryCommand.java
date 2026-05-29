package com.kakeibo.application.usecase.category;

import java.util.UUID;

public record CreateCategoryCommand(
    String name,
    Long budgetAmount,
    UUID userId
) {}
