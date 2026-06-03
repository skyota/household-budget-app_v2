package com.kakeibo.application.usecase.incomesetting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.IncomeSettingNotFoundException;
import com.kakeibo.domain.model.income.IncomeSetting;
import com.kakeibo.domain.repository.IncomeSettingRepository;

@Service
public class DeleteIncomeSettingUseCaseImpl implements DeleteIncomeSettingUseCase {
    private final IncomeSettingRepository incomeSettingRepository;

    public DeleteIncomeSettingUseCaseImpl(IncomeSettingRepository incomeSettingRepository) {
        this.incomeSettingRepository = incomeSettingRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteIncomeSettingCommand command) {
        IncomeSetting incomeSetting = incomeSettingRepository.findByIdAndUserId(command.incomeSettingId(), command.userId())
            .orElseThrow(() -> new IncomeSettingNotFoundException("収入が見つかりません。"));

        incomeSettingRepository.delete(incomeSetting);
    }
}
