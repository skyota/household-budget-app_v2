package com.kakeibo.domain.model.user;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.kakeibo.domain.exception.DomainValidationException;

public class User {
    private final UUID id;
    private String username;
    private String hashedPassword;
    private String apiToken;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public User(UUID id, String username, String hashedPassword, String apiToken, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        if (id == null) {
            throw new DomainValidationException("IDは必須です。");
        }

        if (username == null || username.isBlank()) { // String型は空白のみ（"　"）という意味の値も取れるため
            throw new DomainValidationException("ユーザー名は必須です。");
        }

        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new DomainValidationException("パスワードは必須です。");
        }

        if (createdAt == null) {
            throw new DomainValidationException("作成日時は必須です。");
        }

        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.apiToken = apiToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getApiToken() {
        return apiToken;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean hasSameUsername(String username) {
        return this.username.equals(username);
    }
}
