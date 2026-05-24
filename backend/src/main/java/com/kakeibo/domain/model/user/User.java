package com.kakeibo.domain.model.user;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {
    private final UUID id;
    private String username;
    private String hashedPassword;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public User(UUID id, String username, String hashedPassword, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("IDは必須です。");
        }
        
        if (username == null || username.isBlank()) { // String型は空白のみ（"　"）という意味の値も取れるため
            throw new IllegalArgumentException("ユーザー名は必須です。");
        }

        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("パスワードは必須です。");
        }

        if (createdAt == null) {
            throw new IllegalArgumentException("作成日時は必須です。");
        }

        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
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
