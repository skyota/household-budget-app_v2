package com.kakeibo.application.usecase.investrecord;

import java.util.List;

import com.kakeibo.application.usecase.shared.PaginationResult;

public record ListInvestRecordsResult(
    List<InvestRecordResult> data,
    PaginationResult pagination
) {}
