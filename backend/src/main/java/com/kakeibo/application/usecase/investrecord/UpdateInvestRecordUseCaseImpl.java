package com.kakeibo.application.usecase.investrecord;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.InvestRecordNotFoundException;
import com.kakeibo.domain.model.invest.InvestRecord;
import com.kakeibo.domain.repository.InvestRecordRepository;

@Service
public class UpdateInvestRecordUseCaseImpl implements UpdateInvestRecordUseCase {
    private final InvestRecordRepository investRecordRepository;

    public UpdateInvestRecordUseCaseImpl(InvestRecordRepository investRecordRepository) {
        this.investRecordRepository = investRecordRepository;
    }

    @Override
    @Transactional
    public InvestRecordResult update(UpdateInvestRecordCommand command) {
        InvestRecord existing = investRecordRepository.findByIdAndUserId(command.investRecordId(), command.userId())
            .orElseThrow(() -> new InvestRecordNotFoundException("投資履歴が見つかりません。"));

        InvestRecord updated = new InvestRecord(
            existing.getId(),
            command.amount(),
            command.investDate(),
            command.investType(),
            command.memo(),
            existing.getUserId()
        );

        InvestRecord saved = investRecordRepository.save(updated);

        return InvestRecordResult.from(saved);
    }
}
