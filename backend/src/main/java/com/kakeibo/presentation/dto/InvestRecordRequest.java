package com.kakeibo.presentation.dto;

import java.time.LocalDate;

import com.kakeibo.domain.model.invest.InvestType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InvestRecordRequest(
    @Min(value = 0, message = "金額は0以上で設定してください。")
    long amount,
    @NotNull(message = "投資日は必須です。")
    LocalDate investDate,
    @NotNull(message = "投資区分は必須です。")
    InvestType investType,
    String memo
) {}
