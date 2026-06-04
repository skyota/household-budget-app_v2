package com.kakeibo.application.usecase.incomesetting;

import java.util.List;

import com.kakeibo.application.usecase.shared.PaginationResult;

public record ListIncomeSettingsResult(
    List<IncomeSettingResult> data,
    PaginationResult pagination
) {}
