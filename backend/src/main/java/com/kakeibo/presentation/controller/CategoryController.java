package com.kakeibo.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakeibo.application.usecase.category.CategoryResult;
import com.kakeibo.application.usecase.category.CreateCategoryCommand;
import com.kakeibo.application.usecase.category.CreateCategoryResult;
import com.kakeibo.application.usecase.category.CreateCategoryUseCase;
import com.kakeibo.application.usecase.category.DeleteCategoryCommand;
import com.kakeibo.application.usecase.category.DeleteCategoryUseCase;
import com.kakeibo.application.usecase.category.ListCategoriesQuery;
import com.kakeibo.application.usecase.category.ListCategoriesUseCase;
import com.kakeibo.application.usecase.category.UpdateCategoryCommand;
import com.kakeibo.application.usecase.category.UpdateCategoryResult;
import com.kakeibo.application.usecase.category.UpdateCategoryUseCase;
import com.kakeibo.presentation.dto.CategoryRequest;
import com.kakeibo.presentation.dto.CategoryResponse;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CreateCategoryUseCase createCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryController(CreateCategoryUseCase createCategoryUseCase, ListCategoriesUseCase listCategoriesUseCase, UpdateCategoryUseCase updateCategoryUseCase, DeleteCategoryUseCase deleteCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
        @RequestBody CategoryRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        CreateCategoryResult result = createCategoryUseCase.create(
            new CreateCategoryCommand(request.name(), request.budgetAmount(), userId)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new CategoryResponse(result.id(), result.name(), result.budgetAmount()));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list(
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        List<CategoryResult> result = listCategoriesUseCase.list(
            new ListCategoriesQuery(userId)
        );

        return ResponseEntity.ok(
            result.stream()
                .map(item -> new CategoryResponse(item.id(), item.name(), item.budgetAmount()))
                .toList()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
        @PathVariable Long id,
        @RequestBody CategoryRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        UpdateCategoryResult result = updateCategoryUseCase.update(
            new UpdateCategoryCommand(id, request.name(), request.budgetAmount(), userId)
        );

        return ResponseEntity.ok(
            new CategoryResponse(result.id(), result.name(), result.budgetAmount())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        deleteCategoryUseCase.delete(
            new DeleteCategoryCommand(id, userId)
        );

        return ResponseEntity.noContent().build();
    }
}
