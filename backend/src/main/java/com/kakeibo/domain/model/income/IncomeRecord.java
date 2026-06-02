package com.kakeibo.domain.model.income;

import java.time.LocalDate;
import java.util.UUID;

import com.kakeibo.domain.exception.DomainValidationException;

public class IncomeRecord {
    private final Long id;
    private String title;
    private long amount;
    private LocalDate incomeDate;
    private String memo;
    private boolean isRegular;
    private final UUID userId;

    public IncomeRecord(Long id, String title, long amount, LocalDate incomeDate, String memo, boolean isRegular, UUID userId) {
        if (title == null || title.isBlank()) throw new DomainValidationException("タイトルは必須です。");
        if (amount < 0) throw new DomainValidationException("金額は0以上で設定してください。");
        if (incomeDate == null) throw new DomainValidationException("収入日は必須です。");
        if (userId == null) throw new DomainValidationException("ユーザーIDは必須です。");

        this.id = id;
        this.title = title;
        this.amount = amount;
        this.incomeDate = incomeDate;
        this.memo = memo;
        this.isRegular = isRegular;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public long getAmount() { return amount; }
    public LocalDate getIncomeDate() { return incomeDate; }
    public String getMemo() { return memo; }
    public boolean isRegular() { return isRegular; }
    public UUID getUserId() { return userId; }
}
