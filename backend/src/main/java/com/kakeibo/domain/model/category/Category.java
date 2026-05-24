package com.kakeibo.domain.model.category;

import java.util.UUID;

public class Category {
    private final Long id;
    private String name;
    private BudgetAmount budgetAmount;
    private final UUID userId;

    public Category(Long id, String name, BudgetAmount budgetAmount, UUID userId) {
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