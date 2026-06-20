package com.kakeibo.application.usecase.incomesetting;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.income.IncomeRecord;
import com.kakeibo.domain.model.income.IncomeSetting;
import com.kakeibo.domain.repository.IncomeRecordRepository;
import com.kakeibo.domain.repository.IncomeSettingRepository;

@Service
public class AutoGenerateIncomeRecordsUseCaseImpl implements AutoGenerateIncomeRecordsUseCase {

    private static final Logger log = LoggerFactory.getLogger(AutoGenerateIncomeRecordsUseCaseImpl.class);

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
            try {
                if (!setting.isAutoGenerate()) {
                    continue;
                }

                // amount=0 は意味のあるレコードにならないためスキップ
                if (setting.getAmount() == 0) {
                    continue;
                }

                // incomeDate が null の場合は NPE を避けてスキップ
                if (setting.getIncomeDate() == null) {
                    log.warn("IncomeSetting id={} has null incomeDate. Skipping.", setting.getId());
                    continue;
                }

                LocalDate incomeDate = setting.getIncomeDate().resolveActualDate(currentMonth);

                if (!incomeDate.isAfter(today)) {
                    // 重複チェック: userId + isRegular=true + title + 当月 の組み合わせで判定。
                    // 同一ユーザーで同一タイトルの収入設定が複数ある場合、2件目以降はスキップされる制限がある。
                    boolean alreadyCreated = incomeRecordRepository
                        .existsRegularIncomeByUserIdAndTitleAndIncomeDateBetween(
                            setting.getUserId(), setting.getTitle(), startOfMonth, endOfMonth);

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
            } catch (Exception e) {
                log.error("Failed to auto-generate income record for setting id={}.", setting.getId(), e);
            }
        }
    }
}
