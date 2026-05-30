package com.kakeibo.application.usecase.auth;

import java.util.UUID;

public record AuthenticationSessionResult(UUID userId) {
    public AuthenticationSessionResult {
        if (userId == null) {
            throw new IllegalStateException("認証済みユーザーIDは必須です。");
        }
    }
}
