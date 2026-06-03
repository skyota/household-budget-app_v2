package com.kakeibo.application.usecase.incomesetting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.income.IncomeSetting;
import com.kakeibo.domain.model.shared.MonthlyDay;
import com.kakeibo.domain.repository.IncomeSettingRepository;

@Service
public class CreateIncomeSettingUseCaseImpl implements CreateIncomeSettingUseCase {
    private final IncomeSettingRepository incomeSettingRepository;

    public CreateIncomeSettingUseCaseImpl(IncomeSettingRepository incomeSettingRepository) {
        this.incomeSettingRepository = incomeSettingRepository;
    }

    @Override
    @Transactional
    public IncomeSettingResult create(CreateIncomeSettingCommand command) {
        IncomeSetting incomeSetting = new IncomeSetting(
            null,
            command.title(),
            command.amount(),
            new MonthlyDay(command.incomeDate()),
            command.memo(),
            command.isAutoGenerate(),
            command.userId()
        );

        IncomeSetting saved = incomeSettingRepository.save(incomeSetting);

        return IncomeSettingResult.from(saved);
    }
}
