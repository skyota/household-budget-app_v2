package com.kakeibo.application.usecase.investrecord;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.invest.InvestRecord;
import com.kakeibo.domain.repository.InvestRecordRepository;

@Service
public class CreateInvestRecordUseCaseImpl implements CreateInvestRecordUseCase {
    private final InvestRecordRepository investRecordRepository;

    public CreateInvestRecordUseCaseImpl(InvestRecordRepository investRecordRepository) {
        this.investRecordRepository = investRecordRepository;
    }

    @Override
    @Transactional
    public InvestRecordResult create(CreateInvestRecordCommand command) {
        InvestRecord investRecord = new InvestRecord(
            null,
            command.amount(),
            command.investDate(),
            command.investType(),
            command.memo(),
            command.userId()
        );

        InvestRecord saved = investRecordRepository.save(investRecord);

        return InvestRecordResult.from(saved);
    }
}
