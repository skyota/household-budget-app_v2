package com.kakeibo.presentation.dto;

import java.util.UUID;

public record RegisterResponse(
    UUID id,
    String username
) {}
