package com.kakeibo.infrastructure.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.kakeibo.domain.model.user.LoginSession;
import com.kakeibo.domain.model.user.SessionId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "login_sessions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginSessionEntity {
    @Id
    private String id;
    private UUID userId;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastUsedAt;
    @Column(nullable = true)
    private OffsetDateTime revokedAt;

    public static LoginSessionEntity fromModel(LoginSession session) {
        LoginSessionEntity entity = new LoginSessionEntity();
        entity.setId(session.getId().value()); // SessionId → Stringに変換
        entity.setUserId(session.getUserId());
        entity.setExpiresAt(session.getExpiresAt());
        entity.setCreatedAt(session.getCreatedAt());
        entity.setLastUsedAt(session.getLastUsedAt());
        entity.setRevokedAt(session.getRevokedAt());
        return entity;
    }

    public LoginSession toModel() {
        return new LoginSession(
            new SessionId(id), // String → SessionIdに変換
            userId,
            expiresAt,
            createdAt,
            lastUsedAt,
            revokedAt
        );
    }
}
