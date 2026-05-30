package com.kakeibo.domain.model.category;

import com.kakeibo.domain.exception.DomainValidationException;

public record BudgetAmount(long value) {
    public BudgetAmount {
        if (value < 0) {
            throw new DomainValidationException("予算額にマイナスの値は設定できません。");
        }
    }
}
