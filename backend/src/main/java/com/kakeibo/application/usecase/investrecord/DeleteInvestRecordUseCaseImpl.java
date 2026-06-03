package com.kakeibo.application.usecase.investrecord;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.InvestRecordNotFoundException;
import com.kakeibo.domain.model.invest.InvestRecord;
import com.kakeibo.domain.repository.InvestRecordRepository;

@Service
public class DeleteInvestRecordUseCaseImpl implements DeleteInvestRecordUseCase {
    private final InvestRecordRepository investRecordRepository;

    public DeleteInvestRecordUseCaseImpl(InvestRecordRepository investRecordRepository) {
        this.investRecordRepository = investRecordRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteInvestRecordCommand command) {
        InvestRecord investRecord = investRecordRepository.findByIdAndUserId(command.investRecordId(), command.userId())
            .orElseThrow(() -> new InvestRecordNotFoundException("投資履歴が見つかりません。"));

        investRecordRepository.delete(investRecord);
    }
}
