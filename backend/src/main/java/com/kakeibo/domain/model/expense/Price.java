package com.kakeibo.domain.model.expense;

public record Price(long value) {
    public Price {
        if (value < 0) {
            throw new IllegalArgumentException("支出金額にマイナスの値は設定できません。");
        }
    }
}
