package com.kakeibo.application.usecase.auth;

public record LoginCommand(
    String username,
    String password
) {}
