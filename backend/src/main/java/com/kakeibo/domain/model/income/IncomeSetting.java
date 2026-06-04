package com.kakeibo.domain.model.income;

import java.util.UUID;

import com.kakeibo.domain.exception.DomainValidationException;
import com.kakeibo.domain.model.shared.MonthlyDay;

public class IncomeSetting {
    private final Long id;
    private String title;
    private long amount;
    private MonthlyDay incomeDate;
    private String memo;
    private boolean isAutoGenerate;
    private final UUID userId;

    public IncomeSetting(Long id, String title, long amount, MonthlyDay incomeDate, String memo, boolean isAutoGenerate, UUID userId) {
        if (title == null || title.isBlank()) throw new DomainValidationException("タイトルは必須です。");
        if (amount < 0) throw new DomainValidationException("金額は0以上で設定してください。");
        if (userId == null) throw new DomainValidationException("ユーザーIDは必須です。");

        this.id = id;
        this.title = title;
        this.amount = amount;
        this.incomeDate = incomeDate;
        this.memo = memo;
        this.isAutoGenerate = isAutoGenerate;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public long getAmount() { return amount; }
    public MonthlyDay getIncomeDate() { return incomeDate; }
    public String getMemo() { return memo; }
    public boolean isAutoGenerate() { return isAutoGenerate; }
    public UUID getUserId() { return userId; }
}
