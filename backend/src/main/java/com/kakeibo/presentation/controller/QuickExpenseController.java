package com.kakeibo.presentation.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakeibo.application.usecase.category.CategoryResult;
import com.kakeibo.application.usecase.category.ListCategoriesQuery;
import com.kakeibo.application.usecase.category.ListCategoriesUseCase;
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
    private final ListCategoriesUseCase listCategoriesUseCase;

    public QuickExpenseController(
        CreateExpenseUseCase createExpenseUseCase,
        ListCategoriesUseCase listCategoriesUseCase
    ) {
        this.createExpenseUseCase = createExpenseUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
    }

    // カテゴリ一覧をShortcutから取得するためのエンドポイント
    // {"カテゴリ名": id} 形式で返すことでShortcutsのキー一覧からカテゴリ名だけを表示できる
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Long>> listCategories(
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        List<CategoryResult> results = listCategoriesUseCase.list(new ListCategoriesQuery(userId));
        Map<String, Long> response = new LinkedHashMap<>();
        results.forEach(c -> response.put(c.name(), c.id()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/expense")
    public ResponseEntity<ExpenseResponse> create(
        @Valid @RequestBody QuickExpenseRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        String title = (request.title() != null && !request.title().isBlank())
            ? request.title()
            : "支出";

        // dateがnullの場合は今日の日付を使用
        LocalDate expenseDate = (request.date() != null) ? request.date() : LocalDate.now();

        CreateExpenseResult result = createExpenseUseCase.create(
            new CreateExpenseCommand(title, request.price(), expenseDate, request.categoryId(), null, userId)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ExpenseResponse(result.id(), result.title(), result.price(), result.expenseDate(), result.categoryId(), result.memo()));
    }
}
