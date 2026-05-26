package com.kakeibo.application.usecase.auth;

import com.kakeibo.domain.model.user.SessionId;

public interface LogoutUseCase {
    void logout(SessionId sessionId);
}
