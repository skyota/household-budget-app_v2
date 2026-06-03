package com.kakeibo.application.usecase.asset;

public interface GetAssetSummaryUseCase {
    AssetSummaryResult get(GetAssetSummaryQuery query);
}
