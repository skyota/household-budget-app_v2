package com.kakeibo.domain.model.expense;

import java.time.LocalDate;
import java.util.UUID;

public class Expense {
    private final Long id;
    private String title;
    private Price price;
    private LocalDate expenseDate;
    private Long categoryId;
    private String memo;
    private final UUID userId;

    public Expense(Long id, String title, Price price, LocalDate expenseDate, Long categoryId, String memo, UUID userId) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.expenseDate = expenseDate;
        this.categoryId = categoryId;
        this.memo = memo;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Price getPrice() {
        return price;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getMemo() {
        return memo;
    }
    
    public UUID getUserId() {
        return userId;
    }
}
