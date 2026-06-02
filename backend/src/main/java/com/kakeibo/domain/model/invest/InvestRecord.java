package com.kakeibo.domain.model.invest;

import java.time.LocalDate;
import java.util.UUID;

import com.kakeibo.domain.exception.DomainValidationException;

public class InvestRecord {
    private final Long id;
    private long amount;
    private LocalDate investDate;
    private InvestType investType;
    private String memo;
    private final UUID userId;

    public InvestRecord(Long id, long amount, LocalDate investDate, InvestType investType, String memo, UUID userId) {
        if (amount < 0) throw new DomainValidationException("金額は0以上で設定してください。");
        if (investDate == null) throw new DomainValidationException("投資日は必須です。");
        if (investType == null) throw new DomainValidationException("投資区分は必須です。");
        if (userId == null) throw new DomainValidationException("ユーザーIDは必須です。");

        this.id = id;
        this.amount = amount;
        this.investDate = investDate;
        this.investType = investType;
        this.memo = memo;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public long getAmount() { return amount; }
    public LocalDate getInvestDate() { return investDate; }
    public InvestType getInvestType() { return investType; }
    public String getMemo() { return memo; }
    public UUID getUserId() { return userId; }
}
