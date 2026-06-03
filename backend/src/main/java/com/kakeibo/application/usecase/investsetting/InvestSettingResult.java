package com.kakeibo.application.usecase.investsetting;

import com.kakeibo.domain.model.invest.InvestSetting;

public record InvestSettingResult(
    long amount,
    int investDate
) {
    public static InvestSettingResult from(InvestSetting investSetting) {
        return new InvestSettingResult(
            investSetting.getAmount(),
            investSetting.getInvestDate().value()
        );
    }
}
