package com.kakeibo.presentation.dto;

import java.util.List;

public record FixedExpenseListResponse(
    List<FixedExpenseResponse> data,
    int currentPage,
    int perPage,
    long totalCount,
    int totalPages
) {}
