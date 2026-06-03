package com.kakeibo.application.usecase.investsetting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.InvestSettingNotFoundException;
import com.kakeibo.domain.model.invest.InvestSetting;
import com.kakeibo.domain.repository.InvestSettingRepository;

@Service
public class GetInvestSettingUseCaseImpl implements GetInvestSettingUseCase {
    private final InvestSettingRepository investSettingRepository;

    public GetInvestSettingUseCaseImpl(InvestSettingRepository investSettingRepository) {
        this.investSettingRepository = investSettingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public InvestSettingResult get(GetInvestSettingQuery query) {
        InvestSetting setting = investSettingRepository.findByUserId(query.userId())
            .orElseThrow(() -> new InvestSettingNotFoundException("投資設定が見つかりません。"));

        return InvestSettingResult.from(setting);
    }
}
