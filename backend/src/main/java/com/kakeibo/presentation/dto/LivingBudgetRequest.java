package com.kakeibo.presentation.dto;

import jakarta.validation.constraints.Min;

public record LivingBudgetRequest(
    @Min(value = 0, message = "生活費予算は0円以上で設定してください。")
    long amount
) {}
