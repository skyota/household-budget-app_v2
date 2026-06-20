package com.kakeibo.application.usecase.investsetting;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.invest.InvestRecord;
import com.kakeibo.domain.model.invest.InvestSetting;
import com.kakeibo.domain.model.invest.InvestType;
import com.kakeibo.domain.repository.InvestRecordRepository;
import com.kakeibo.domain.repository.InvestSettingRepository;

@Service
public class AutoGenerateInvestRecordsUseCaseImpl implements AutoGenerateInvestRecordsUseCase {

    private final InvestSettingRepository investSettingRepository;
    private final InvestRecordRepository investRecordRepository;

    public AutoGenerateInvestRecordsUseCaseImpl(
            InvestSettingRepository investSettingRepository,
            InvestRecordRepository investRecordRepository) {
        this.investSettingRepository = investSettingRepository;
        this.investRecordRepository = investRecordRepository;
    }

    @Override
    @Transactional
    public void execute() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        List<InvestSetting> allSettings = investSettingRepository.findAll();
        if (allSettings.isEmpty()) {
            return;
        }

        for (InvestSetting setting : allSettings) {
            LocalDate investDate = setting.getInvestDate().resolveActualDate(currentMonth);

            if (setting.getAmount() == 0) {
                continue;
            }

            if (!investDate.isAfter(today)) {
                boolean alreadyCreated = investRecordRepository
                    .existsByUserIdAndInvestTypeAndInvestDateBetween(
                        setting.getUserId(), InvestType.REGULAR, startOfMonth, endOfMonth);

                if (!alreadyCreated) {
                    InvestRecord record = new InvestRecord(
                        null,
                        setting.getAmount(),
                        investDate,
                        InvestType.REGULAR,
                        null,
                        setting.getUserId());
                    investRecordRepository.save(record);
                }
            }
        }
    }
}
