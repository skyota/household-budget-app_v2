package com.kakeibo.presentation.dto;

import java.util.List;

public record InvestRecordListResponse(
    List<InvestRecordResponse> data,
    int currentPage,
    int perPage,
    long totalCount,
    int totalPages
) {}
