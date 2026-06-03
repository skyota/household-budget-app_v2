package com.kakeibo.application.usecase.cashbalance;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.cashbalance.CashBalance;
import com.kakeibo.domain.repository.CashBalanceRepository;

@Service
public class CreateCashBalanceUseCaseImpl implements CreateCashBalanceUseCase {
    private final CashBalanceRepository cashBalanceRepository;

    public CreateCashBalanceUseCaseImpl(CashBalanceRepository cashBalanceRepository) {
        this.cashBalanceRepository = cashBalanceRepository;
    }

    @Override
    @Transactional
    public CashBalanceResult create(CreateCashBalanceCommand command) {
        CashBalance cashBalance = new CashBalance(
            null,
            command.amount(),
            command.balanceDate(),
            command.balanceType(),
            command.memo(),
            command.userId()
        );

        CashBalance saved = cashBalanceRepository.save(cashBalance);

        return CashBalanceResult.from(saved);
    }
}
