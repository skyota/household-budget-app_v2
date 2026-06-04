package com.kakeibo.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IncomeSettingRequest(
    @NotBlank(message = "タイトルは必須です。")
    String title,
    @Min(value = 0, message = "金額は0以上で設定してください。")
    long amount,
    @NotNull(message = "給料日は必須です。")
    Integer incomeDate,
    String memo,
    boolean isAutoGenerate
) {}
