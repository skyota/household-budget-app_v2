package com.kakeibo.application.usecase.category;

import java.util.UUID;

public record DeleteCategoryCommand(
    Long categoryId,
    UUID userId
) {}
