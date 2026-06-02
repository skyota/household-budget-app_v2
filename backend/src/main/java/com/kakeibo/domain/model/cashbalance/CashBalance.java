package com.kakeibo.domain.model.cashbalance;

import java.time.LocalDate;
import java.util.UUID;

import com.kakeibo.domain.exception.DomainValidationException;

public class CashBalance {
    private final Long id;
    private long amount;
    private LocalDate balanceDate;
    private BalanceType balanceType;
    private String memo;
    private final UUID userId;

    public CashBalance(Long id, long amount, LocalDate balanceDate, BalanceType balanceType, String memo, UUID userId) {
        if (amount < 0) throw new DomainValidationException("残高は0以上で設定してください。");
        if (balanceDate == null) throw new DomainValidationException("確認日は必須です。");
        if (balanceType == null) throw new DomainValidationException("残高区分は必須です。");
        if (userId == null) throw new DomainValidationException("ユーザーIDは必須です。");

        this.id = id;
        this.amount = amount;
        this.balanceDate = balanceDate;
        this.balanceType = balanceType;
        this.memo = memo;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public long getAmount() { return amount; }
    public LocalDate getBalanceDate() { return balanceDate; }
    public BalanceType getBalanceType() { return balanceType; }
    public String getMemo() { return memo; }
    public UUID getUserId() { return userId; }
}
