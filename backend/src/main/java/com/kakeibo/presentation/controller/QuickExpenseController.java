package com.kakeibo.presentation.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakeibo.application.usecase.expense.CreateExpenseCommand;
import com.kakeibo.application.usecase.expense.CreateExpenseResult;
import com.kakeibo.application.usecase.expense.CreateExpenseUseCase;
import com.kakeibo.presentation.dto.ExpenseResponse;
import com.kakeibo.presentation.dto.QuickExpenseRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/quick")
public class QuickExpenseController {
    private final CreateExpenseUseCase createExpenseUseCase;

    public QuickExpenseController(CreateExpenseUseCase createExpenseUseCase) {
        this.createExpenseUseCase = createExpenseUseCase;
    }

    @PostMapping("/expense")
    public ResponseEntity<ExpenseResponse> create(
        @Valid @RequestBody QuickExpenseRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        String title = (request.title() != null && !request.title().isBlank())
            ? request.title()
            : "支出";

        CreateExpenseResult result = createExpenseUseCase.create(
            new CreateExpenseCommand(title, request.price(), LocalDate.now(), request.categoryId(), null, userId)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ExpenseResponse(result.id(), result.title(), result.price(), result.expenseDate(), result.categoryId(), result.memo()));
    }
}
