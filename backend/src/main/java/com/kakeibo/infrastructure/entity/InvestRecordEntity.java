package com.kakeibo.infrastructure.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.kakeibo.domain.model.invest.InvestRecord;
import com.kakeibo.domain.model.invest.InvestType;

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
@Table(name = "invest_records")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvestRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long amount;
    private LocalDate investDate;
    @Enumerated(EnumType.STRING)
    private InvestType investType;
    private String memo;
    private UUID userId;

    public static InvestRecordEntity fromModel(InvestRecord investRecord) {
        InvestRecordEntity entity = new InvestRecordEntity();
        entity.setId(investRecord.getId());
        entity.setAmount(investRecord.getAmount());
        entity.setInvestDate(investRecord.getInvestDate());
        entity.setInvestType(investRecord.getInvestType());
        entity.setMemo(investRecord.getMemo());
        entity.setUserId(investRecord.getUserId());
        return entity;
    }

    public InvestRecord toModel() {
        return new InvestRecord(id, amount, investDate, investType, memo, userId);
    }
}
