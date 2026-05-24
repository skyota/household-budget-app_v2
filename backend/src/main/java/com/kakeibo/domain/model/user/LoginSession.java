package com.kakeibo.domain.model.user;

import java.time.OffsetDateTime;
import java.util.UUID;

public class LoginSession {
    private final SessionId id;
    private final UUID userId;
    private final OffsetDateTime expiresAt;
    private final OffsetDateTime createdAt;
    private OffsetDateTime lastUsedAt;
    private OffsetDateTime revokedAt;

    public LoginSession(SessionId id, UUID userId, OffsetDateTime expiresAt, OffsetDateTime createdAt, OffsetDateTime lastUsedAt, OffsetDateTime revokedAt) {
        if (id == null) {
            throw new IllegalArgumentException("セッションIDは必須です。");
        }

        if (userId == null) {
            throw new IllegalArgumentException("ユーザーIDは必須です。");
        }

        if (expiresAt == null) {
            throw new IllegalArgumentException("有効期限は必須です。");
        }

        if (createdAt == null) {
            throw new IllegalArgumentException("作成日時は必須です。");
        }

        this.id = id;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
        this.revokedAt = revokedAt;
    }

    public SessionId getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public OffsetDateTime getRevokedAt() {
        return revokedAt;
    }

    public boolean isExpired(OffsetDateTime now) {
        return !expiresAt.isAfter(now); // expiresAt.isBefore(now)とすると、有効期限と現在時刻が一致するときも期限切れではなく、有効としてしまう
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isActive(OffsetDateTime now) {
        return !isExpired(now) && !isRevoked();
    }
}

