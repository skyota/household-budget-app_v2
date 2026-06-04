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

import com.kakeibo.application.usecase.investrecord.CreateInvestRecordCommand;
import com.kakeibo.application.usecase.investrecord.CreateInvestRecordUseCase;
import com.kakeibo.application.usecase.investrecord.DeleteInvestRecordCommand;
import com.kakeibo.application.usecase.investrecord.DeleteInvestRecordUseCase;
import com.kakeibo.application.usecase.investrecord.InvestRecordResult;
import com.kakeibo.application.usecase.investrecord.ListInvestRecordsQuery;
import com.kakeibo.application.usecase.investrecord.ListInvestRecordsUseCase;
import com.kakeibo.application.usecase.investrecord.UpdateInvestRecordCommand;
import com.kakeibo.application.usecase.investrecord.UpdateInvestRecordUseCase;
import com.kakeibo.presentation.dto.InvestRecordListResponse;
import com.kakeibo.presentation.dto.InvestRecordRequest;
import com.kakeibo.presentation.dto.InvestRecordResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/invest-records")
public class InvestRecordController {
    private final CreateInvestRecordUseCase createInvestRecordUseCase;
    private final ListInvestRecordsUseCase listInvestRecordsUseCase;
    private final UpdateInvestRecordUseCase updateInvestRecordUseCase;
    private final DeleteInvestRecordUseCase deleteInvestRecordUseCase;

    public InvestRecordController(
        CreateInvestRecordUseCase createInvestRecordUseCase,
        ListInvestRecordsUseCase listInvestRecordsUseCase,
        UpdateInvestRecordUseCase updateInvestRecordUseCase,
        DeleteInvestRecordUseCase deleteInvestRecordUseCase
    ) {
        this.createInvestRecordUseCase = createInvestRecordUseCase;
        this.listInvestRecordsUseCase = listInvestRecordsUseCase;
        this.updateInvestRecordUseCase = updateInvestRecordUseCase;
        this.deleteInvestRecordUseCase = deleteInvestRecordUseCase;
    }

    @PostMapping
    public ResponseEntity<InvestRecordResponse> create(
        @Valid @RequestBody InvestRecordRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        InvestRecordResult result = createInvestRecordUseCase.create(new CreateInvestRecordCommand(
            request.amount(),
            request.investDate(),
            request.investType(),
            request.memo(),
            userId
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping
    public ResponseEntity<InvestRecordListResponse> list(
        @RequestAttribute("authenticatedUserId") UUID userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "100") int perPage
    ) {
        var result = listInvestRecordsUseCase.list(new ListInvestRecordsQuery(userId, page, perPage));

        return ResponseEntity.ok(new InvestRecordListResponse(
            result.data().stream().map(this::toResponse).toList(),
            result.pagination().currentPage(),
            result.pagination().perPage(),
            result.pagination().totalCount(),
            result.pagination().totalPages()
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InvestRecordResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody InvestRecordRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        InvestRecordResult result = updateInvestRecordUseCase.update(new UpdateInvestRecordCommand(
            id,
            request.amount(),
            request.investDate(),
            request.investType(),
            request.memo(),
            userId
        ));

        return ResponseEntity.ok(toResponse(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        deleteInvestRecordUseCase.delete(new DeleteInvestRecordCommand(id, userId));

        return ResponseEntity.noContent().build();
    }

    private InvestRecordResponse toResponse(InvestRecordResult result) {
        return new InvestRecordResponse(
            result.id(),
            result.amount(),
            result.investDate(),
            result.investType(),
            result.memo()
        );
    }
}
