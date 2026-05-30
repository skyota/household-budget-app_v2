package com.kakeibo.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank(message = "カテゴリー名は必須です。")
    String name,
    @Min(value = 0, message = "予算額にマイナスの値は設定できません。")
    long budgetAmount
) {}
