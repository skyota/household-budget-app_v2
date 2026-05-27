package com.kakeibo.application.usecase.auth;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.InvalidSessionException;
import com.kakeibo.domain.model.user.LoginSession;
import com.kakeibo.domain.model.user.SessionId;
import com.kakeibo.domain.repository.LoginSessionRepository;

@Service
public class AuthenticationSessionUseCaseImpl implements AuthenticationSessionUseCase {
    private final LoginSessionRepository loginSessionRepository;

    public AuthenticationSessionUseCaseImpl(LoginSessionRepository loginSessionRepository) {
        this.loginSessionRepository = loginSessionRepository;
    }
    

    @Override
    @Transactional
    public AuthenticationSessionResult authenticate(SessionId sessionId) {
        // セッションを取得
        LoginSession session = loginSessionRepository.findById(sessionId)
            .orElseThrow(() -> new InvalidSessionException("セッションが存在しません。"));

        // 有効かチェック（期限切れ・失効済みを両方カバー）
        if (!session.isActive(OffsetDateTime.now())) {
            throw new InvalidSessionException("セッションが無効または期限切れです。");
        }

        // lastUsedAtを現在時刻に更新して保存
        session.touch(OffsetDateTime.now());
        loginSessionRepository.save(session);

        // 認証済みユーザーIDを返す
        return new AuthenticationSessionResult(session.getUserId());
    }
}
