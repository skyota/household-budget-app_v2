package com.kakeibo.application.usecase.fixedexpense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.FixedExpenseNotFoundException;
import com.kakeibo.domain.model.fixedexpense.FixedExpense;
import com.kakeibo.domain.model.shared.MonthlyDay;
import com.kakeibo.domain.repository.FixedExpenseRepository;

@Service
public class UpdateFixedExpenseUseCaseImpl implements UpdateFixedExpenseUseCase {
    private final FixedExpenseRepository fixedExpenseRepository;

    public UpdateFixedExpenseUseCaseImpl(FixedExpenseRepository fixedExpenseRepository) {
        this.fixedExpenseRepository = fixedExpenseRepository;
    }

    @Override
    @Transactional
    public FixedExpenseResult update(UpdateFixedExpenseCommand command) {
        FixedExpense existing = fixedExpenseRepository.findByIdAndUserId(command.fixedExpenseId(), command.userId())
            .orElseThrow(() -> new FixedExpenseNotFoundException("固定費がみつかりません。"));

        FixedExpense updated = new FixedExpense(
            existing.getId(),
            command.title(),
            command.price(),
            new MonthlyDay(command.fixedExpenseDate()),
            command.memo(),
            existing.getUserId()
        );

        FixedExpense saved = fixedExpenseRepository.save(updated);
        
        return FixedExpenseResult.from(saved);
    }
}
