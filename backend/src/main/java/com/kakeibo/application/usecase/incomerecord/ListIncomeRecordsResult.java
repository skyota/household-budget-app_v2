package com.kakeibo.application.usecase.incomerecord;

import java.util.List;

import com.kakeibo.application.usecase.shared.PaginationResult;

public record ListIncomeRecordsResult(
    List<IncomeRecordResult> data,
    PaginationResult pagination
) {}
