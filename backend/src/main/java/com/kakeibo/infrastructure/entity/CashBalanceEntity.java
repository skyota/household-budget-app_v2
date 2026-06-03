package com.kakeibo.infrastructure.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.kakeibo.domain.model.cashbalance.BalanceType;
import com.kakeibo.domain.model.cashbalance.CashBalance;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cash_balances")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashBalanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long amount;
    private LocalDate balanceDate;
    @Enumerated(EnumType.STRING)
    private BalanceType balanceType;
    private String memo;
    private UUID userId;

    public static CashBalanceEntity fromModel(CashBalance cashBalance) {
        CashBalanceEntity entity = new CashBalanceEntity();
        entity.setId(cashBalance.getId());
        entity.setAmount(cashBalance.getAmount());
        entity.setBalanceDate(cashBalance.getBalanceDate());
        entity.setBalanceType(cashBalance.getBalanceType());
        entity.setMemo(cashBalance.getMemo());
        entity.setUserId(cashBalance.getUserId());
        return entity;
    }

    public CashBalance toModel() {
        return new CashBalance(id, amount, balanceDate, balanceType, memo, userId);
    }
}
