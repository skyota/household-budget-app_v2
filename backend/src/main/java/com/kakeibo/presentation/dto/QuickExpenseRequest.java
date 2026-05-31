package com.kakeibo.presentation.dto;

import jakarta.validation.constraints.Min;

public record QuickExpenseRequest(
    @Min(value = 1, message = "金額は1円以上で入力してください。")
    long price,
    String title,
    Long categoryId
) {}
