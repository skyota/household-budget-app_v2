package com.kakeibo.application.usecase.asset;

public record AssetSummaryResult(
    long cashBalance,
    long nisaPrincipal,
    long totalAssets
) {}
