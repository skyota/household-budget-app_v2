package com.kakeibo.application.usecase.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.user.SessionId;
import com.kakeibo.domain.repository.LoginSessionRepository;

@Service
public class LogoutUseCaseImpl implements LogoutUseCase {
    private final LoginSessionRepository loginSessionRepository;

    public LogoutUseCaseImpl(LoginSessionRepository loginSessionRepository) {
        this.loginSessionRepository = loginSessionRepository;
    }

    @Override
    @Transactional
    public void logout(SessionId sessionId) {
        loginSessionRepository.revoke(sessionId);
    }
}
