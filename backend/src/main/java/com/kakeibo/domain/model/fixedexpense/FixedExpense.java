package com.kakeibo.domain.model.fixedexpense;

import java.util.UUID;

import com.kakeibo.domain.exception.DomainValidationException;
import com.kakeibo.domain.model.shared.MonthlyDay;

public class FixedExpense {
    private final Long id;
    private String title;
    private long price;
    private MonthlyDay monthlyDay;
    private String memo;
    private final UUID userId;

    public FixedExpense(Long id, String title, long price, MonthlyDay monthlyDay, String memo, UUID userId) {
        if (title == null || title.isBlank()) throw new DomainValidationException("タイトルは必須です。");
        if (price < 0) throw new DomainValidationException("金額は0以上で設定してください。");
        if (userId == null) throw new DomainValidationException("ユーザーIDは必須です。");

        this.id = id;
        this.title = title;
        this.price = price;
        this.monthlyDay = monthlyDay;
        this.memo = memo;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public long getPrice() { return price; }
    public MonthlyDay getMonthlyDay() { return monthlyDay; }
    public String getMemo() { return memo; }
    public UUID getUserId() { return userId; }
}
