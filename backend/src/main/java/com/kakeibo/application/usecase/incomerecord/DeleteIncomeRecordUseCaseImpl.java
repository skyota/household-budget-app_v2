package com.kakeibo.application.usecase.incomerecord;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.IncomeRecordNotFoundException;
import com.kakeibo.domain.model.income.IncomeRecord;
import com.kakeibo.domain.repository.IncomeRecordRepository;

@Service
public class DeleteIncomeRecordUseCaseImpl implements DeleteIncomeRecordUseCase {
    private final IncomeRecordRepository incomeRecordRepository;

    public DeleteIncomeRecordUseCaseImpl(IncomeRecordRepository incomeRecordRepository) {
        this.incomeRecordRepository = incomeRecordRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteIncomeRecordCommand command) {
        IncomeRecord incomeRecord = incomeRecordRepository.findByIdAndUserId(command.incomeRecordId(), command.userId())
            .orElseThrow(() -> new IncomeRecordNotFoundException("収入が見つかりません。"));
        
        incomeRecordRepository.delete(incomeRecord);
    }
}
