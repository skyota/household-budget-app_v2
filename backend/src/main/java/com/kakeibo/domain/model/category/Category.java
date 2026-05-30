package com.kakeibo.domain.model.category;

import java.util.UUID;

import com.kakeibo.domain.exception.DomainValidationException;

public class Category {
    private final Long id;
    private String name;
    private BudgetAmount budgetAmount;
    private final UUID userId;

    public Category(Long id, String name, BudgetAmount budgetAmount, UUID userId) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("カテゴリー名は必須です。");
        }

        if (budgetAmount == null) {
            throw new DomainValidationException("予算額は必須です。");
        }

        if (userId == null) {
            throw new DomainValidationException("ユーザーIDは必須です。");
        }

        this.id = id;
        this.name = name;
        this.budgetAmount = budgetAmount;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BudgetAmount getBudgetAmount() {
        return budgetAmount;
    }

    public UUID getUserId() {
        return userId;
    }
}