package com.kakeibo.presentation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExpenseRequest(
    @NotBlank(message = "タイトルは必須です。")
    String title,
    @Min(value = 0, message = "支出金額にマイナスの値は設定できません。")
    long price,
    @NotNull(message = "支出日は必須です。")
    LocalDate expenseDate,
    Long categoryId, // nullable
    String memo // nullable
) {}
