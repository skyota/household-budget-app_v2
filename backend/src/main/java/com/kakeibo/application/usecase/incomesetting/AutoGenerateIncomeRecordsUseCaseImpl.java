package com.kakeibo.application.usecase.incomesetting;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.income.IncomeRecord;
import com.kakeibo.domain.model.income.IncomeSetting;
import com.kakeibo.domain.repository.IncomeRecordRepository;
import com.kakeibo.domain.repository.IncomeSettingRepository;

@Service
public class AutoGenerateIncomeRecordsUseCaseImpl implements AutoGenerateIncomeRecordsUseCase {

    private final IncomeSettingRepository incomeSettingRepository;
    private final IncomeRecordRepository incomeRecordRepository;

    public AutoGenerateIncomeRecordsUseCaseImpl(
            IncomeSettingRepository incomeSettingRepository,
            IncomeRecordRepository incomeRecordRepository) {
        this.incomeSettingRepository = incomeSettingRepository;
        this.incomeRecordRepository = incomeRecordRepository;
    }

    @Override
    @Transactional
    public void execute() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        List<IncomeSetting> allSettings = incomeSettingRepository.findAll();
        if (allSettings.isEmpty()) {
            return;
        }

        for (IncomeSetting setting : allSettings) {
            if (!setting.isAutoGenerate() || setting.getAmount() == 0) {
                continue;
            }

            LocalDate incomeDate = setting.getIncomeDate().resolveActualDate(currentMonth);

            if (!incomeDate.isAfter(today)) {
                boolean alreadyCreated = incomeRecordRepository
                    .existsByUserIdAndIsRegularAndTitleAndIncomeDateBetween(
                        setting.getUserId(), true, setting.getTitle(), startOfMonth, endOfMonth);

                if (!alreadyCreated) {
                    IncomeRecord record = new IncomeRecord(
                        null,
                        setting.getTitle(),
                        setting.getAmount(),
                        incomeDate,
                        null,
                        true,
                        setting.getUserId());
                    incomeRecordRepository.save(record);
                }
            }
        }
    }
}
