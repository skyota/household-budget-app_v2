package com.kakeibo.infrastructure.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.kakeibo.domain.model.income.IncomeRecord;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "income_records")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncomeRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private long amount;
    private LocalDate incomeDate;
    private String memo;
    private boolean isRegular;
    private UUID userId;

    public static IncomeRecordEntity fromModel(IncomeRecord incomeRecord) {
        IncomeRecordEntity entity = new IncomeRecordEntity();
        entity.setId(incomeRecord.getId());
        entity.setTitle(incomeRecord.getTitle());
        entity.setAmount(incomeRecord.getAmount());
        entity.setIncomeDate(incomeRecord.getIncomeDate());
        entity.setMemo(incomeRecord.getMemo());
        entity.setRegular(incomeRecord.isRegular());
        entity.setUserId(incomeRecord.getUserId());
        return entity;
    }

    public IncomeRecord toModel() {
        return new IncomeRecord(id, title, amount, incomeDate, memo, isRegular, userId);
    }
}
