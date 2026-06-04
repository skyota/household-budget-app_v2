package com.kakeibo.presentation.dto;

public record AssetSummaryResponse(
    long cashBalance,
    long nisaPrincipal,
    long totalAssets
) {}
