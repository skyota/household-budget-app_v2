package com.kakeibo.presentation.dto;

import java.util.List;

public record IncomeRecordListResponse(
    List<IncomeRecordResponse> data,
    int currentPage,
    int perPage,
    long totalCount,
    int totalPages
) {}
