package com.kakeibo.application.usecase.cashbalance;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.CashBalanceNotFoundException;
import com.kakeibo.domain.model.cashbalance.CashBalance;
import com.kakeibo.domain.repository.CashBalanceRepository;

@Service
public class DeleteCashBalanceUseCaseImpl implements DeleteCashBalanceUseCase {
    private final CashBalanceRepository cashBalanceRepository;

    public DeleteCashBalanceUseCaseImpl(CashBalanceRepository cashBalanceRepository) {
        this.cashBalanceRepository = cashBalanceRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteCashBalanceCommand command) {
        CashBalance cashBalance = cashBalanceRepository.findByIdAndUserId(command.cashBalanceId(), command.userId())
            .orElseThrow(() -> new CashBalanceNotFoundException("残高が見つかりません。"));
        
        cashBalanceRepository.delete(cashBalance);
    }
}
