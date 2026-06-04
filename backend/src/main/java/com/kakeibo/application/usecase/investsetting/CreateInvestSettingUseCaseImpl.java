package com.kakeibo.application.usecase.investsetting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.invest.InvestSetting;
import com.kakeibo.domain.model.shared.MonthlyDay;
import com.kakeibo.domain.repository.InvestSettingRepository;

@Service
public class CreateInvestSettingUseCaseImpl implements CreateInvestSettingUseCase {
    private final InvestSettingRepository investSettingRepository;

    public CreateInvestSettingUseCaseImpl(InvestSettingRepository investSettingRepository) {
        this.investSettingRepository = investSettingRepository;
    }

    @Override
    @Transactional
    public InvestSettingResult create(CreateInvestSettingCommand command) {
        InvestSetting investSetting = new InvestSetting(
            command.userId(),
            command.amount(),
            new MonthlyDay(command.investDate())
        );

        InvestSetting saved = investSettingRepository.save(investSetting);

        return InvestSettingResult.from(saved);
    }
}
