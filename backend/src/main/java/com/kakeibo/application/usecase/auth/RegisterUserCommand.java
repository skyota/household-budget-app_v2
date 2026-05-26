package com.kakeibo.application.usecase.auth;

public record RegisterUserCommand(
    String username,
    String password
) {}
