package com.kakeibo.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakeibo.application.usecase.asset.GetAssetSummaryQuery;
import com.kakeibo.application.usecase.asset.GetAssetSummaryUseCase;
import com.kakeibo.presentation.dto.AssetSummaryResponse;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    private final GetAssetSummaryUseCase getAssetSummaryUseCase;

    public AssetController(GetAssetSummaryUseCase getAssetSummaryUseCase) {
        this.getAssetSummaryUseCase = getAssetSummaryUseCase;
    }

    @GetMapping("/summary")
    public ResponseEntity<AssetSummaryResponse> getSummary(
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        var result = getAssetSummaryUseCase.get(new GetAssetSummaryQuery(userId));

        return ResponseEntity.ok(new AssetSummaryResponse(
            result.cashBalance(),
            result.nisaPrincipal(),
            result.totalAssets()
        ));
    }
}
