package com.kakeibo.application.usecase.auth;

public interface LoginUseCase {
    LoginResult login(LoginCommand command);
}
