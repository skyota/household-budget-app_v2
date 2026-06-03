package com.kakeibo.application.usecase.investsetting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.InvestSettingNotFoundException;
import com.kakeibo.domain.model.invest.InvestSetting;
import com.kakeibo.domain.model.shared.MonthlyDay;
import com.kakeibo.domain.repository.InvestSettingRepository;

@Service
public class UpdateInvestSettingUseCaseImpl implements UpdateInvestSettingUseCase {
    private final InvestSettingRepository investSettingRepository;

    public UpdateInvestSettingUseCaseImpl(InvestSettingRepository investSettingRepository) {
        this.investSettingRepository = investSettingRepository;
    }

    @Override
    @Transactional
    public InvestSettingResult update(UpdateInvestSettingCommand command) {
        InvestSetting existing = investSettingRepository.findByUserId(command.userId())
            .orElseThrow(() -> new InvestSettingNotFoundException("投資設定が見つかりません。"));

        InvestSetting updated = new InvestSetting(
            existing.getUserId(),
            command.amount(),
            new MonthlyDay(command.investDate())
        );

        InvestSetting saved = investSettingRepository.save(updated);

        return InvestSettingResult.from(saved);
    }
}
