package com.kakeibo.domain.model.invest;

import java.util.UUID;

import com.kakeibo.domain.exception.DomainValidationException;
import com.kakeibo.domain.model.shared.MonthlyDay;

public class InvestSetting {
    private final UUID userId;
    private long amount;
    private MonthlyDay investDate;

    public InvestSetting(UUID userId, long amount, MonthlyDay investDate) {
        if (userId == null) throw new DomainValidationException("ユーザーIDは必須です。");
        if (amount < 0) throw new DomainValidationException("金額は0以上で設定してください。");

        this.userId = userId;
        this.amount = amount;
        this.investDate = investDate;
    }

    public UUID getUserId() { return userId; }
    public long getAmount() { return amount; }
    public MonthlyDay getInvestDate() { return investDate; }
}
