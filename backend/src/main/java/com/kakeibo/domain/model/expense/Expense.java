package com.kakeibo.domain.model.expense;

import java.time.LocalDate;
import java.util.UUID;

import com.kakeibo.domain.exception.DomainValidationException;

public class Expense {
    private final Long id;
    private String title;
    private Price price;
    private LocalDate expenseDate;
    private Long categoryId;
    private String memo;
    private final UUID userId;
    private final Long fixedExpenseId;

    public Expense(Long id, String title, Price price, LocalDate expenseDate, Long categoryId, String memo, UUID userId) {
        this(id, title, price, expenseDate, categoryId, memo, userId, null);
    }

    public Expense(Long id, String title, Price price, LocalDate expenseDate, Long categoryId, String memo, UUID userId, Long fixedExpenseId) {
        if (title == null || title.isBlank()) {
            throw new DomainValidationException("タイトルは必須です。");
        }

        if (price == null) {
            throw new DomainValidationException("金額は必須です。");
        }

        if (expenseDate == null) {
            throw new DomainValidationException("支出日は必須です。");
        }

        if (userId == null) {
            throw new DomainValidationException("ユーザーIDは必須です。");
        }

        this.id = id;
        this.title = title;
        this.price = price;
        this.expenseDate = expenseDate;
        this.categoryId = categoryId;
        this.memo = memo;
        this.userId = userId;
        this.fixedExpenseId = fixedExpenseId;
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

    public Long getFixedExpenseId() {
        return fixedExpenseId;
    }
}
