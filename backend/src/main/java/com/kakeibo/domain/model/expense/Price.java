package com.kakeibo.domain.model.expense;

import com.kakeibo.domain.exception.DomainValidationException;

public record Price(long value) {
    public Price {
        if (value < 0) {
            throw new DomainValidationException("支出金額にマイナスの値は設定できません。");
        }
    }
}
