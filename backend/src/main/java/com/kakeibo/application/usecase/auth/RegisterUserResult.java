package com.kakeibo.application.usecase.auth;

import java.util.UUID;

public record RegisterUserResult(
    UUID id,
    String username
) {}
