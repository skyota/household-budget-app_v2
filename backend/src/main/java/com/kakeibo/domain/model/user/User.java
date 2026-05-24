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
