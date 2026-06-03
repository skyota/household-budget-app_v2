package com.kakeibo.application.usecase.investrecord;

import java.time.LocalDate;

import com.kakeibo.domain.model.invest.InvestRecord;
import com.kakeibo.domain.model.invest.InvestType;

public record InvestRecordResult(
    Long id,
    long amount,
    LocalDate investDate,
    InvestType investType,
    String memo
) {
    public static InvestRecordResult from(InvestRecord investRecord) {
        return new InvestRecordResult(
            investRecord.getId(),
            investRecord.getAmount(),
            investRecord.getInvestDate(),
            investRecord.getInvestType(),
            investRecord.getMemo()
        );
    }
}
