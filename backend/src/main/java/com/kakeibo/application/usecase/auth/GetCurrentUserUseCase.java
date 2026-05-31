package com.kakeibo.application.usecase.auth;

public interface GetCurrentUserUseCase {
    GetCurrentUserResult get(GetCurrentUserQuery query);
}
