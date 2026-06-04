package com.kakeibo.presentation.dto;

import java.time.LocalDate;

import com.kakeibo.domain.model.cashbalance.BalanceType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CashBalanceRequest(
    @Min(value = 0, message = "残高は0以上で設定してください。")
    long amount,
    @NotNull(message = "確認日は必須です。")
    LocalDate balanceDate,
    @NotNull(message = "残高区分は必須です。")
    BalanceType balanceType,
    String memo
) {}
