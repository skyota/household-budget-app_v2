package com.kakeibo.presentation.dto;

import java.util.List;

public record ExpenseListResponse(
    List<ExpenseListItemResponse> expenses,
    int currentPage,
    int perPage,
    long totalCount,
    int totalPages
) {}
