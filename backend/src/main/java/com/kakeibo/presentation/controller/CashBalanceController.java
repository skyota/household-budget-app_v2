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

import com.kakeibo.application.usecase.cashbalance.CashBalanceResult;
import com.kakeibo.application.usecase.cashbalance.CreateCashBalanceCommand;
import com.kakeibo.application.usecase.cashbalance.CreateCashBalanceUseCase;
import com.kakeibo.application.usecase.cashbalance.DeleteCashBalanceCommand;
import com.kakeibo.application.usecase.cashbalance.DeleteCashBalanceUseCase;
import com.kakeibo.application.usecase.cashbalance.ListCashBalanceQuery;
import com.kakeibo.application.usecase.cashbalance.ListCashBalanceUseCase;
import com.kakeibo.application.usecase.cashbalance.UpdateCashBalanceCommand;
import com.kakeibo.application.usecase.cashbalance.UpdateCashBalanceUseCase;
import com.kakeibo.presentation.dto.CashBalanceListResponse;
import com.kakeibo.presentation.dto.CashBalanceRequest;
import com.kakeibo.presentation.dto.CashBalanceResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cash-balances")
public class CashBalanceController {
    private final CreateCashBalanceUseCase createCashBalanceUseCase;
    private final ListCashBalanceUseCase listCashBalanceUseCase;
    private final UpdateCashBalanceUseCase updateCashBalanceUseCase;
    private final DeleteCashBalanceUseCase deleteCashBalanceUseCase;

    public CashBalanceController(
        CreateCashBalanceUseCase createCashBalanceUseCase,
        ListCashBalanceUseCase listCashBalanceUseCase,
        UpdateCashBalanceUseCase updateCashBalanceUseCase,
        DeleteCashBalanceUseCase deleteCashBalanceUseCase
    ) {
        this.createCashBalanceUseCase = createCashBalanceUseCase;
        this.listCashBalanceUseCase = listCashBalanceUseCase;
        this.updateCashBalanceUseCase = updateCashBalanceUseCase;
        this.deleteCashBalanceUseCase = deleteCashBalanceUseCase;
    }

    @PostMapping
    public ResponseEntity<CashBalanceResponse> create(
        @Valid @RequestBody CashBalanceRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        CashBalanceResult result = createCashBalanceUseCase.create(new CreateCashBalanceCommand(
            request.amount(),
            request.balanceDate(),
            request.balanceType(),
            request.memo(),
            userId
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @GetMapping
    public ResponseEntity<CashBalanceListResponse> list(
        @RequestAttribute("authenticatedUserId") UUID userId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "100") int perPage
    ) {
        var result = listCashBalanceUseCase.list(new ListCashBalanceQuery(userId, page, perPage));

        return ResponseEntity.ok(new CashBalanceListResponse(
            result.data().stream().map(this::toResponse).toList(),
            result.pagination().currentPage(),
            result.pagination().perPage(),
            result.pagination().totalCount(),
            result.pagination().totalPages()
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CashBalanceResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody CashBalanceRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        CashBalanceResult result = updateCashBalanceUseCase.update(new UpdateCashBalanceCommand(
            id,
            request.amount(),
            request.balanceDate(),
            request.balanceType(),
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
        deleteCashBalanceUseCase.delete(new DeleteCashBalanceCommand(id, userId));
        
        return ResponseEntity.noContent().build();
    }

    private CashBalanceResponse toResponse(CashBalanceResult result) {
        return new CashBalanceResponse(
            result.id(),
            result.amount(),
            result.balanceDate(),
            result.balanceType(),
            result.memo()
        );
    }
}
