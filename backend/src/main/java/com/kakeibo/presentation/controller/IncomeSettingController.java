package com.kakeibo.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kakeibo.application.usecase.incomesetting.CreateIncomeSettingCommand;
import com.kakeibo.application.usecase.incomesetting.CreateIncomeSettingUseCase;
import com.kakeibo.application.usecase.incomesetting.DeleteIncomeSettingCommand;
import com.kakeibo.application.usecase.incomesetting.DeleteIncomeSettingUseCase;
import com.kakeibo.application.usecase.incomesetting.IncomeSettingResult;
import com.kakeibo.application.usecase.incomesetting.ListIncomeSettingsQuery;
import com.kakeibo.application.usecase.incomesetting.ListIncomeSettingsUseCase;
import com.kakeibo.application.usecase.incomesetting.UpdateIncomeSettingCommand;
import com.kakeibo.application.usecase.incomesetting.UpdateIncomeSettingUseCase;
import com.kakeibo.presentation.dto.IncomeSettingResponse;
import com.kakeibo.presentation.dto.IncomeSettingListResponse;
import com.kakeibo.presentation.dto.IncomeSettingRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/income-settings")
public class IncomeSettingController {
    private final CreateIncomeSettingUseCase createIncomeSettingUseCase;
    private final ListIncomeSettingsUseCase listIncomeSettingsUseCase;
    private final UpdateIncomeSettingUseCase updateIncomeSettingUseCase;
    private final DeleteIncomeSettingUseCase deleteIncomeSettingUseCase;

    public IncomeSettingController(
        CreateIncomeSettingUseCase createIncomeSettingUseCase,
        ListIncomeSettingsUseCase listIncomeSettingsUseCase,
        UpdateIncomeSettingUseCase updateIncomeSettingUseCase,
        DeleteIncomeSettingUseCase deleteIncomeSettingUseCase
    ) {
        this.createIncomeSettingUseCase = createIncomeSettingUseCase;
        this.listIncomeSettingsUseCase = listIncomeSettingsUseCase;
        this.updateIncomeSettingUseCase = updateIncomeSettingUseCase;
        this.deleteIncomeSettingUseCase = deleteIncomeSettingUseCase;
    }

    @PostMapping
    public ResponseEntity<IncomeSettingResponse> create(
        @Valid @RequestBody IncomeSettingRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        IncomeSettingResult result = createIncomeSettingUseCase.create(new CreateIncomeSettingCommand(
            request.title(),
            request.amount(),
            request.incomeDate(),
            request.memo(),
            request.isAutoGenerate(),
            userId
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping
    public ResponseEntity<IncomeSettingListResponse> list(
        @RequestAttribute("authenticatedUserId") UUID userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "100") int perPage
    ) {
        var result = listIncomeSettingsUseCase.list(new ListIncomeSettingsQuery(userId, page, perPage));

        return ResponseEntity.ok(new IncomeSettingListResponse(
            result.data().stream().map(this::toResponse).toList(),
            result.pagination().currentPage(),
            result.pagination().perPage(),
            result.pagination().totalCount(),
            result.pagination().totalPages()
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IncomeSettingResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody IncomeSettingRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        IncomeSettingResult result = updateIncomeSettingUseCase.update(new UpdateIncomeSettingCommand(
            id,
            request.title(),
            request.amount(),
            request.incomeDate(),
            request.memo(),
            request.isAutoGenerate(),
            userId
        ));

        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        deleteIncomeSettingUseCase.delete(new DeleteIncomeSettingCommand(id, userId));

        return ResponseEntity.noContent().build();
    }

    private IncomeSettingResponse toResponse(IncomeSettingResult result) {
        return new IncomeSettingResponse(
            result.id(),
            result.title(),
            result.amount(),
            result.incomeDate(),
            result.memo(),
            result.isAutoGenerate()
        );
    }
}
