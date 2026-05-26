package com.kakeibo.application.usecase.auth;

import com.kakeibo.domain.model.user.SessionId;

public interface AuthenticationSessionUseCase {
    // Cookieから取得したsessionIdを受け取る→セッションが存在するか確認する→有効期限切れでないか確認する→有効ならAuthenticationSessionResultを返す
    AuthenticationSessionResult authenticate(SessionId sessionId);
}
