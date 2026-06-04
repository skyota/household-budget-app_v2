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

import com.kakeibo.application.usecase.incomerecord.CreateIncomeRecordCommand;
import com.kakeibo.application.usecase.incomerecord.CreateIncomeRecordUseCase;
import com.kakeibo.application.usecase.incomerecord.DeleteIncomeRecordCommand;
import com.kakeibo.application.usecase.incomerecord.DeleteIncomeRecordUseCase;
import com.kakeibo.application.usecase.incomerecord.IncomeRecordResult;
import com.kakeibo.application.usecase.incomerecord.ListIncomeRecordsQuery;
import com.kakeibo.application.usecase.incomerecord.ListIncomeRecordsUseCase;
import com.kakeibo.application.usecase.incomerecord.UpdateIncomeRecordCommand;
import com.kakeibo.application.usecase.incomerecord.UpdateIncomeRecordUseCase;
import com.kakeibo.presentation.dto.IncomeRecordListResponse;
import com.kakeibo.presentation.dto.IncomeRecordRequest;
import com.kakeibo.presentation.dto.IncomeRecordResponse;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/income-records")
public class IncomeRecordController {
    private final CreateIncomeRecordUseCase createIncomeRecordUseCase;
    private final ListIncomeRecordsUseCase listIncomeRecordsUseCase;
    private final UpdateIncomeRecordUseCase updateIncomeRecordUseCase;
    private final DeleteIncomeRecordUseCase deleteIncomeRecordUseCase;

    public IncomeRecordController(
        CreateIncomeRecordUseCase createIncomeRecordUseCase,
        ListIncomeRecordsUseCase listIncomeRecordsUseCase,
        UpdateIncomeRecordUseCase updateIncomeRecordUseCase,
        DeleteIncomeRecordUseCase deleteIncomeRecordUseCase
    ) {
        this.createIncomeRecordUseCase = createIncomeRecordUseCase;
        this.listIncomeRecordsUseCase = listIncomeRecordsUseCase;
        this.updateIncomeRecordUseCase = updateIncomeRecordUseCase;
        this.deleteIncomeRecordUseCase = deleteIncomeRecordUseCase;
    }

    @PostMapping
    public ResponseEntity<IncomeRecordResponse> create(
        @Valid @RequestBody IncomeRecordRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        IncomeRecordResult result = createIncomeRecordUseCase.create(new CreateIncomeRecordCommand(
            request.title(),
            request.amount(),
            request.incomeDate(),
            request.memo(),
            request.isRegular(),
            userId
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping
    public ResponseEntity<IncomeRecordListResponse> list(
        @RequestAttribute("authenticatedUserId") UUID userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "100") int perPage
    ) {
        var result = listIncomeRecordsUseCase.list(new ListIncomeRecordsQuery(userId, page, perPage));

        return ResponseEntity.ok(new IncomeRecordListResponse(
            result.data().stream().map(this::toResponse).toList(),
            result.pagination().currentPage(),
            result.pagination().perPage(),
            result.pagination().totalCount(),
            result.pagination().totalPages()
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IncomeRecordResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody IncomeRecordRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        IncomeRecordResult result = updateIncomeRecordUseCase.update(new UpdateIncomeRecordCommand(
            id,
            request.title(),
            request.amount(),
            request.incomeDate(),
            request.memo(),
            request.isRegular(),
            userId
        ));

        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        deleteIncomeRecordUseCase.delete(new DeleteIncomeRecordCommand(id, userId));

        return ResponseEntity.noContent().build();
    }

    private IncomeRecordResponse toResponse(IncomeRecordResult result) {
        return new IncomeRecordResponse(
            result.id(),
            result.title(),
            result.amount(),
            result.incomeDate(),
            result.memo(),
            result.isRegular()
        );
    }
}
