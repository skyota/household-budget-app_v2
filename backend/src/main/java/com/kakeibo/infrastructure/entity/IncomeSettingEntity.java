package com.kakeibo.infrastructure.entity;

import java.util.UUID;

import com.kakeibo.domain.model.income.IncomeSetting;
import com.kakeibo.domain.model.shared.MonthlyDay;

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
@Table(name = "income_settings")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IncomeSettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private long amount;
    private int incomeDate;
    private String memo;
    private boolean isAutoGenerate;
    private UUID userId;

    public static IncomeSettingEntity fromModel(IncomeSetting incomeSetting) {
        IncomeSettingEntity entity = new IncomeSettingEntity();
        entity.setId(incomeSetting.getId());
        entity.setTitle(incomeSetting.getTitle());
        entity.setAmount(incomeSetting.getAmount());
        entity.setIncomeDate(incomeSetting.getIncomeDate().value());
        entity.setMemo(incomeSetting.getMemo());
        entity.setAutoGenerate(incomeSetting.isAutoGenerate());
        entity.setUserId(incomeSetting.getUserId());
        return entity;
    }

    public IncomeSetting toModel() {
        return new IncomeSetting(id, title, amount, new MonthlyDay(incomeDate), memo, isAutoGenerate, userId);
    }
}
