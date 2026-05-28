package com.kakeibo.presentation.dto;

import java.util.UUID;

public record LoginResponse(
    UUID id,
    String username
) {}
