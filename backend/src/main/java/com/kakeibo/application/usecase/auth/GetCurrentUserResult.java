package com.kakeibo.application.usecase.auth;

import java.util.UUID;

public record GetCurrentUserResult(UUID userId, String username) {}
