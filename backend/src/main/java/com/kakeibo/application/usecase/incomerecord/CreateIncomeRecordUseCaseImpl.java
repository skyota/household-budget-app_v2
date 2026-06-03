package com.kakeibo.application.usecase.incomerecord;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.income.IncomeRecord;
import com.kakeibo.domain.repository.IncomeRecordRepository;

@Service
public class CreateIncomeRecordUseCaseImpl implements CreateIncomeRecordUseCase {
    private final IncomeRecordRepository incomeRecordRepository;

    public CreateIncomeRecordUseCaseImpl(IncomeRecordRepository incomeRecordRepository) {
        this.incomeRecordRepository = incomeRecordRepository;
    }

    @Override
    @Transactional
    public IncomeRecordResult create(CreateIncomeRecordCommand command) {
        IncomeRecord incomeRecord = new IncomeRecord(
            null,
            command.title(),
            command.amount(),
            command.incomeDate(),
            command.memo(),
            command.isRegular(),
            command.userId()
        );

        IncomeRecord saved = incomeRecordRepository.save(incomeRecord);

        return IncomeRecordResult.from(saved);
    }
}
