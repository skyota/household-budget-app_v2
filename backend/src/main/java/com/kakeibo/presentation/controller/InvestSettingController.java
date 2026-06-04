package com.kakeibo.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakeibo.application.usecase.investsetting.CreateInvestSettingCommand;
import com.kakeibo.application.usecase.investsetting.CreateInvestSettingUseCase;
import com.kakeibo.application.usecase.investsetting.GetInvestSettingQuery;
import com.kakeibo.application.usecase.investsetting.GetInvestSettingUseCase;
import com.kakeibo.application.usecase.investsetting.InvestSettingResult;
import com.kakeibo.application.usecase.investsetting.UpdateInvestSettingCommand;
import com.kakeibo.application.usecase.investsetting.UpdateInvestSettingUseCase;
import com.kakeibo.presentation.dto.InvestSettingRequest;
import com.kakeibo.presentation.dto.InvestSettingResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/invest-settings")
public class InvestSettingController {
    private final CreateInvestSettingUseCase createInvestSettingUseCase;
    private final GetInvestSettingUseCase getInvestSettingUseCase;
    private final UpdateInvestSettingUseCase updateInvestSettingUseCase;

    public InvestSettingController(
        CreateInvestSettingUseCase createInvestSettingUseCase,
        UpdateInvestSettingUseCase updateInvestSettingUseCase,
        GetInvestSettingUseCase getInvestSettingUseCase
    ) {
        this.createInvestSettingUseCase = createInvestSettingUseCase;
        this.updateInvestSettingUseCase = updateInvestSettingUseCase;
        this.getInvestSettingUseCase = getInvestSettingUseCase;
    }

    @PostMapping
    public ResponseEntity<InvestSettingResponse> create(
        @Valid @RequestBody InvestSettingRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        InvestSettingResult result = createInvestSettingUseCase.create(new CreateInvestSettingCommand(
            request.amount(),
            request.investDate(),
            userId
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping
    public ResponseEntity<InvestSettingResponse> get(
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        var result = getInvestSettingUseCase.get(new GetInvestSettingQuery(userId));

        return ResponseEntity.ok(new InvestSettingResponse(
            result.amount(),
            result.investDate()
        ));
    }

    @PatchMapping
    public ResponseEntity<InvestSettingResponse> update(
        @Valid @RequestBody InvestSettingRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        InvestSettingResult result = updateInvestSettingUseCase.update(new UpdateInvestSettingCommand(
            request.amount(),
            request.investDate(),
            userId
        ));

        return ResponseEntity.ok(toResponse(result));
    }

    private InvestSettingResponse toResponse(InvestSettingResult result) {
        return new InvestSettingResponse(
            result.amount(),
            result.investDate()
        );
    }
}
