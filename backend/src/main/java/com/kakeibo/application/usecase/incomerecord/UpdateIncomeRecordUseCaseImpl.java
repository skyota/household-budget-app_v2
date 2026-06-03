package com.kakeibo.application.usecase.incomerecord;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.IncomeRecordNotFoundException;
import com.kakeibo.domain.model.income.IncomeRecord;
import com.kakeibo.domain.repository.IncomeRecordRepository;

@Service
public class UpdateIncomeRecordUseCaseImpl implements UpdateIncomeRecordUseCase {
    private final IncomeRecordRepository incomeRecordRepository;

    public UpdateIncomeRecordUseCaseImpl(IncomeRecordRepository incomeRecordRepository) {
        this.incomeRecordRepository = incomeRecordRepository;
    }

    @Override
    @Transactional
    public IncomeRecordResult update(UpdateIncomeRecordCommand command) {
        IncomeRecord existing = incomeRecordRepository.findByIdAndUserId(command.incomeRecordId(), command.userId())
            .orElseThrow(() -> new IncomeRecordNotFoundException("収入が見つかりません。"));

        IncomeRecord updated = new IncomeRecord(
            existing.getId(),
            command.title(),
            command.amount(),
            command.incomeDate(),
            command.memo(),
            command.isRegular(),
            existing.getUserId()
        );

        IncomeRecord saved = incomeRecordRepository.save(updated);

        return IncomeRecordResult.from(saved);
    }
}
