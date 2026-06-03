package com.kakeibo.application.usecase.incomesetting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.IncomeSettingNotFoundException;
import com.kakeibo.domain.model.income.IncomeSetting;
import com.kakeibo.domain.model.shared.MonthlyDay;
import com.kakeibo.domain.repository.IncomeSettingRepository;

@Service
public class UpdateIncomeSettingUseCaseImpl implements UpdateIncomeSettingUseCase {
    private final IncomeSettingRepository incomeSettingRepository;

    public UpdateIncomeSettingUseCaseImpl(IncomeSettingRepository incomeSettingRepository) {
        this.incomeSettingRepository = incomeSettingRepository;
    }

    @Override
    @Transactional
    public IncomeSettingResult update(UpdateIncomeSettingCommand command) {
        IncomeSetting existing = incomeSettingRepository.findByIdAndUserId(command.incomeSettingId(), command.userId())
            .orElseThrow(() -> new IncomeSettingNotFoundException("収入が見つかりません。"));

        IncomeSetting updated = new IncomeSetting(
            existing.getId(),
            command.title(),
            command.amount(),
            new MonthlyDay(command.incomeDate()),
            command.memo(),
            command.isAutoGenerate(),
            existing.getUserId()
        );

        IncomeSetting saved = incomeSettingRepository.save(updated);

        return IncomeSettingResult.from(saved);
    }
}
