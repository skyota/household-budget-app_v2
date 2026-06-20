package com.kakeibo.infrastructure.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kakeibo.application.usecase.fixedexpense.AutoGenerateFixedExpensesUseCase;
import com.kakeibo.application.usecase.investsetting.AutoGenerateInvestRecordsUseCase;

@Component
public class FixedExpenseScheduler {

    private static final Logger log = LoggerFactory.getLogger(FixedExpenseScheduler.class);

    private final AutoGenerateFixedExpensesUseCase autoGenerateFixedExpensesUseCase;
    private final AutoGenerateInvestRecordsUseCase autoGenerateInvestRecordsUseCase;

    public FixedExpenseScheduler(
            AutoGenerateFixedExpensesUseCase autoGenerateFixedExpensesUseCase,
            AutoGenerateInvestRecordsUseCase autoGenerateInvestRecordsUseCase) {
        this.autoGenerateFixedExpensesUseCase = autoGenerateFixedExpensesUseCase;
        this.autoGenerateInvestRecordsUseCase = autoGenerateInvestRecordsUseCase;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Daily auto-generation triggered on startup.");
        runDailyAutoGeneration();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Tokyo")
    public void runDailyAutoGeneration() {
        log.info("Fixed expense auto-generation started.");
        try {
            autoGenerateFixedExpensesUseCase.execute();
            log.info("Fixed expense auto-generation completed.");
        } catch (Exception e) {
            log.error("Fixed expense auto-generation failed.", e);
        }

        log.info("Invest record auto-generation started.");
        try {
            autoGenerateInvestRecordsUseCase.execute();
            log.info("Invest record auto-generation completed.");
        } catch (Exception e) {
            log.error("Invest record auto-generation failed.", e);
        }
    }
}
