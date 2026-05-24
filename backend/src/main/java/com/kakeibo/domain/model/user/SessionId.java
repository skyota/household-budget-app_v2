package com.kakeibo.domain.model.user;

public record SessionId(String value) {
    public SessionId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("セッションIDは必須です。");
        }
    }
}
