package com.kakeibo.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InvestSettingRequest(
    @Min(value = 0, message = "金額は0以上で設定してください。")
    long amount,
    @NotNull(message = "積立日は必須です。")
    Integer investDate
) {}
