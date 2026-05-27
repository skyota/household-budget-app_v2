package com.kakeibo.application.usecase.auth;

public interface AuthenticationSessionUseCase {
    // Cookieから取得したsessionIdを受け取る→セッションが存在するか確認する→有効期限切れでないか確認する→有効ならAuthenticationSessionResultを返す
    AuthenticationSessionResult authenticate(String sessionId);
}
