package com.kakeibo.infrastructure.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kakeibo.domain.model.user.LoginSession;
import com.kakeibo.domain.model.user.SessionId;
import com.kakeibo.domain.repository.LoginSessionRepository;
import com.kakeibo.infrastructure.entity.LoginSessionEntity;

@Repository
public class JpaLoginSessionRepository implements LoginSessionRepository {
    private final SpringDataLoginSessionRepository springDataLoginSessionRepository;

    public JpaLoginSessionRepository(SpringDataLoginSessionRepository springDataLoginSessionRepository) {
        this.springDataLoginSessionRepository = springDataLoginSessionRepository;
    }

    @Override
    public Optional<LoginSession> findById(SessionId sessionId) {
        return springDataLoginSessionRepository.findById(sessionId.value())
            .map(LoginSessionEntity::toModel);
    }

    @Override
    public LoginSession save(LoginSession session) {
        LoginSessionEntity entity = LoginSessionEntity.fromModel(session);
        return springDataLoginSessionRepository.save(entity).toModel();
    }

    @Override
    public void revoke(SessionId sessionId) {
        springDataLoginSessionRepository.findById(sessionId.value())
            .ifPresent(entity -> {
                entity.setRevokedAt(OffsetDateTime.now());
                springDataLoginSessionRepository.save(entity);
            });
    }
}
