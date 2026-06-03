package com.kakeibo.application.usecase.shared;

public record PaginationResult(
    int currentPage,
    int perPage,
    long totalCount,
    int totalPages
) {}
