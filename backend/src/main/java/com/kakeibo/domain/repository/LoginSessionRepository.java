package com.kakeibo.domain.repository;

import java.util.Optional;

import com.kakeibo.domain.model.user.LoginSession;
import com.kakeibo.domain.model.user.SessionId;

public interface LoginSessionRepository {
    Optional<LoginSession> findById(SessionId sessionId);
    LoginSession save(LoginSession loginSession);
    void revoke(SessionId sessionId);
}
