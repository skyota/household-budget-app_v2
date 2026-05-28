package com.kakeibo.application.usecase.auth;

import java.util.UUID;

public record LoginResult(
    String sessionId,
    UUID userId,
    String username
) {}
