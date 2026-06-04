package com.kakeibo.application.usecase.incomesetting;

import java.util.UUID;

import com.kakeibo.domain.model.income.IncomeSetting;

public record IncomeSettingResult(
    Long id,
    String title,
    long amount,
    int incomeDate,
    String memo,
    boolean isAutoGenerate,
    UUID userId
) {
    public static IncomeSettingResult from(IncomeSetting incomeSetting) {
        return new IncomeSettingResult(
            incomeSetting.getId(),
            incomeSetting.getTitle(),
            incomeSetting.getAmount(),
            incomeSetting.getIncomeDate().value(),
            incomeSetting.getMemo(),
            incomeSetting.isAutoGenerate(),
            incomeSetting.getUserId()
        );
    }
}
