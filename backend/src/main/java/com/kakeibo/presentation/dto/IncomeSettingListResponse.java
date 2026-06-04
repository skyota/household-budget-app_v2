package com.kakeibo.presentation.dto;

import java.util.List;

public record IncomeSettingListResponse(
    List<IncomeSettingResponse> data,
    int currentPage,
    int perPage,
    long totalCount,
    int totalPages
) {}
