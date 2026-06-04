package com.kakeibo.application.usecase.incomerecord;

import java.time.LocalDate;

import com.kakeibo.domain.model.income.IncomeRecord;

public record IncomeRecordResult(
    Long id,
    String title,
    long amount,
    LocalDate incomeDate,
    String memo,
    boolean isRegular
) {
    public static IncomeRecordResult from(IncomeRecord incomeRecord) {
        return new IncomeRecordResult(
            incomeRecord.getId(),
            incomeRecord.getTitle(),
            incomeRecord.getAmount(),
            incomeRecord.getIncomeDate(),
            incomeRecord.getMemo(),
            incomeRecord.isRegular()
        );
    }
}
