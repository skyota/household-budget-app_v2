package com.kakeibo.domain.model.user;

import com.kakeibo.domain.exception.DomainValidationException;

public record SessionId(String value) {
    public SessionId {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("セッションIDは必須です。");
        }
    }
}
