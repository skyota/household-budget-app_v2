package com.kakeibo.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import com.kakeibo.domain.exception.DomainValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kakeibo.application.usecase.expense.CreateExpenseCommand;
import com.kakeibo.application.usecase.expense.CreateExpenseResult;
import com.kakeibo.application.usecase.expense.CreateExpenseUseCase;
import com.kakeibo.application.usecase.expense.DeleteExpenseCommand;
import com.kakeibo.application.usecase.expense.DeleteExpenseUseCase;
import com.kakeibo.application.usecase.expense.ListExpensesQuery;
import com.kakeibo.application.usecase.expense.ListExpensesResult;
import com.kakeibo.application.usecase.expense.ListExpensesUseCase;
import com.kakeibo.application.usecase.expense.UpdateExpenseCommand;
import com.kakeibo.application.usecase.expense.UpdateExpenseResult;
import com.kakeibo.application.usecase.expense.UpdateExpenseUseCase;
import com.kakeibo.presentation.dto.ExpenseListItemResponse;
import com.kakeibo.presentation.dto.ExpenseListResponse;
import com.kakeibo.presentation.dto.ExpenseRequest;
import com.kakeibo.presentation.dto.ExpenseResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final CreateExpenseUseCase createExpenseUseCase;
    private final ListExpensesUseCase listExpensesUseCase;
    private final UpdateExpenseUseCase updateExpenseUseCase;
    private final DeleteExpenseUseCase deleteExpenseUseCase;

    public ExpenseController(CreateExpenseUseCase createExpenseUseCase, ListExpensesUseCase listExpensesUseCase, UpdateExpenseUseCase updateExpenseUseCase, DeleteExpenseUseCase deleteExpenseUseCase) {
        this.createExpenseUseCase = createExpenseUseCase;
        this.listExpensesUseCase = listExpensesUseCase;
        this.updateExpenseUseCase = updateExpenseUseCase;
        this.deleteExpenseUseCase = deleteExpenseUseCase;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(
        @Valid @RequestBody ExpenseRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        CreateExpenseResult result = createExpenseUseCase.create(new CreateExpenseCommand(
            request.title(),
            request.price(),
            request.expenseDate(),
            request.categoryId(),
            request.memo(),
            userId
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ExpenseResponse(result.id(), result.title(), result.price(), result.expenseDate(), result.categoryId(), result.memo()));
    }

    @GetMapping
    public ResponseEntity<ExpenseListResponse> list(
        @RequestAttribute("authenticatedUserId") UUID userId,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int perPage
    ) {
        if ((year == null) != (month == null)) {
            throw new DomainValidationException("yearとmonthは両方指定するか、両方省略してください。");
        }
        if (month != null && (month < 1 || month > 12)) {
            throw new DomainValidationException("monthは1〜12の範囲で指定してください。");
        }
        if (year != null && (year < 1900 || year > 9999)) {
            throw new DomainValidationException("yearは1900〜9999の範囲で指定してください。");
        }

        if (page < 1) page = 1;
        if (perPage < 1) perPage = 20;
        if (perPage > 200) perPage = 200;

        ListExpensesResult result = listExpensesUseCase.list(
            new ListExpensesQuery(userId, page, perPage, year, month)
        );

        return ResponseEntity.ok(new ExpenseListResponse(
            result.expenses().stream()
                .map(item -> new ExpenseListItemResponse(
                    item.id(),
                    item.title(),
                    item.price(),
                    item.expenseDate(),
                    item.categoryId(),
                    item.categoryName(),
                    item.memo()
                ))
                .toList(),
            result.pagination().currentPage(),
            result.pagination().perPage(),
            result.pagination().totalCount(),
            result.pagination().totalPages()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody ExpenseRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        UpdateExpenseResult result = updateExpenseUseCase.update(new UpdateExpenseCommand(
            id,
            request.title(),
            request.price(),
            request.expenseDate(),
            request.categoryId(),
            request.memo(),
            userId
        ));

        return ResponseEntity.ok(new ExpenseResponse(
            result.id(),
            result.title(),
            result.price(),
            result.expenseDate(),
            result.categoryId(),
            result.memo()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        deleteExpenseUseCase.delete(new DeleteExpenseCommand(id, userId));

        return ResponseEntity.noContent().build();
    }
}
