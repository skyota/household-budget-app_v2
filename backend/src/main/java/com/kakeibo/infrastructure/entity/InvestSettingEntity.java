package com.kakeibo.infrastructure.entity;

import java.util.UUID;

import com.kakeibo.domain.model.invest.InvestSetting;
import com.kakeibo.domain.model.shared.MonthlyDay;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invest_settings")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvestSettingEntity {
    @Id
    private UUID userId;
    private long amount;
    private int investDate;
    
    public static InvestSettingEntity fromModel(InvestSetting investSetting) {
        InvestSettingEntity entity = new InvestSettingEntity();
        entity.setUserId(investSetting.getUserId());
        entity.setAmount(investSetting.getAmount());
        entity.setInvestDate(investSetting.getInvestDate().value());
        return entity;
    }

    public InvestSetting toModel() {
        return new InvestSetting(userId, amount, new MonthlyDay(investDate));
    }
}
