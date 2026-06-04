package com.kakeibo.presentation.dto;

import java.util.List;

public record CashBalanceListResponse(
    List<CashBalanceResponse> data,
    int currentPage,
    int perPage,
    long totalCount,
    int totalPages
) {}
