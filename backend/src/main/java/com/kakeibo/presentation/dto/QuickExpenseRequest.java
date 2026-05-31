package com.kakeibo.presentation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;

public record QuickExpenseRequest(
    @Min(value = 1, message = "金額は1円以上で入力してください。")
    long price,
    String title,
    LocalDate date,   // nullの場合はコントローラーで今日の日付を使用
    Long categoryId
) {}
