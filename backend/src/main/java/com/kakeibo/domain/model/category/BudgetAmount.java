package com.kakeibo.domain.model.category;

public record BudgetAmount(long value) {
    public BudgetAmount {
        if (value < 0) {
            throw new IllegalArgumentException("予算額にマイナスの値は設定できません。");
        }
    }
}
