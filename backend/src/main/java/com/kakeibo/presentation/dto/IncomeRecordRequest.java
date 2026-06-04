package com.kakeibo.presentation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IncomeRecordRequest(
    @NotBlank(message = "タイトルは必須です。")
    String title,
    @Min(value = 0, message = "金額は0以上で設定してください。")
    long amount,
    @NotNull(message = "収入日は必須です。")
    LocalDate incomeDate,
    String memo,
    boolean isRegular
) {}
