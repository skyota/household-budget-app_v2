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

import com.kakeibo.application.usecase.fixedexpense.CreateFixedExpenseCommand;
import com.kakeibo.application.usecase.fixedexpense.CreateFixedExpenseUseCase;
import com.kakeibo.application.usecase.fixedexpense.DeleteFixedExpenseCommand;
import com.kakeibo.application.usecase.fixedexpense.DeleteFixedExpenseUseCase;
import com.kakeibo.application.usecase.fixedexpense.FixedExpenseResult;
import com.kakeibo.application.usecase.fixedexpense.ListFixedExpensesQuery;
import com.kakeibo.application.usecase.fixedexpense.ListFixedExpensesUseCase;
import com.kakeibo.application.usecase.fixedexpense.UpdateFixedExpenseCommand;
import com.kakeibo.application.usecase.fixedexpense.UpdateFixedExpenseUseCase;
import com.kakeibo.presentation.dto.FixedExpenseListResponse;
import com.kakeibo.presentation.dto.FixedExpenseRequest;
import com.kakeibo.presentation.dto.FixedExpenseResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/fixed-expenses")
public class FixedExpenseController {
    private final CreateFixedExpenseUseCase createFixedExpenseUseCase;
    private final ListFixedExpensesUseCase listFixedExpensesUseCase;
    private final UpdateFixedExpenseUseCase updateFixedExpenseUseCase;
    private final DeleteFixedExpenseUseCase deleteFixedExpenseUseCase;

    public FixedExpenseController(
        CreateFixedExpenseUseCase createFixedExpenseUseCase,
        ListFixedExpensesUseCase listFixedExpensesUseCase,
        UpdateFixedExpenseUseCase updateFixedExpenseUseCase,
        DeleteFixedExpenseUseCase deleteFixedExpenseUseCase
    ) {
        this.createFixedExpenseUseCase = createFixedExpenseUseCase;
        this.listFixedExpensesUseCase = listFixedExpensesUseCase;
        this.updateFixedExpenseUseCase = updateFixedExpenseUseCase;
        this.deleteFixedExpenseUseCase = deleteFixedExpenseUseCase;
    }

    @PostMapping
    public ResponseEntity<FixedExpenseResponse> create(
        @Valid @RequestBody FixedExpenseRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        FixedExpenseResult result = createFixedExpenseUseCase.create(new CreateFixedExpenseCommand(
            request.title(),
            request.price(),
            request.fixedExpenseDate(),
            request.memo(),
            userId
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping
    public ResponseEntity<FixedExpenseListResponse> list(
        @RequestAttribute("authenticatedUserId") UUID userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "100") int perPage
    ) {
        var result = listFixedExpensesUseCase.list(new ListFixedExpensesQuery(userId, page, perPage));

        return ResponseEntity.ok(new FixedExpenseListResponse(
            result.data().stream().map(this::toResponse).toList(),
            result.pagination().currentPage(),
            result.pagination().perPage(),
            result.pagination().totalCount(),
            result.pagination().totalPages()
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FixedExpenseResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody FixedExpenseRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        FixedExpenseResult result = updateFixedExpenseUseCase.update(new UpdateFixedExpenseCommand(
            id,
            request.title(),
            request.price(),
            request.fixedExpenseDate(),
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
        deleteFixedExpenseUseCase.delete(new DeleteFixedExpenseCommand(id, userId));
        return ResponseEntity.noContent().build();
    }

    private FixedExpenseResponse toResponse(FixedExpenseResult result) {
        return new FixedExpenseResponse(
            result.id(),
            result.title(),
            result.price(),
            result.fixedExpenseDate(),
            result.memo()
        );
    }
}
