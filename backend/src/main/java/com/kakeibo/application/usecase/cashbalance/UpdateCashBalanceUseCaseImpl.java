package com.kakeibo.application.usecase.cashbalance;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.CashBalanceNotFoundException;
import com.kakeibo.domain.model.cashbalance.CashBalance;
import com.kakeibo.domain.repository.CashBalanceRepository;

@Service
public class UpdateCashBalanceUseCaseImpl implements UpdateCashBalanceUseCase {
    private final CashBalanceRepository cashBalanceRepository;

    public UpdateCashBalanceUseCaseImpl(CashBalanceRepository cashBalanceRepository) {
        this.cashBalanceRepository = cashBalanceRepository;
    }

    @Override
    @Transactional
    public CashBalanceResult update(UpdateCashBalanceCommand command) {
        CashBalance existing = cashBalanceRepository.findByIdAndUserId(command.cashBalanceId(), command.userId())
            .orElseThrow(() -> new CashBalanceNotFoundException("残高が見つかりません。"));

        CashBalance updated = new CashBalance(
            existing.getId(),
            command.amount(),
            command.balanceDate(),
            command.balanceType(),
            command.memo(),
            existing.getUserId()
        );

        CashBalance saved = cashBalanceRepository.save(updated);

        return CashBalanceResult.from(saved);
    }
}
