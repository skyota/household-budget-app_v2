package com.kakeibo.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryResponse(
    Long id,
    String name,
    long budgetAmount,
    @JsonProperty("isSystem") boolean isSystem
) {}
