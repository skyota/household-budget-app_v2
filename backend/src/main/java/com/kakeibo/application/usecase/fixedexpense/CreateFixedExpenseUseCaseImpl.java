package com.kakeibo.application.usecase.fixedexpense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.fixedexpense.FixedExpense;
import com.kakeibo.domain.model.shared.MonthlyDay;
import com.kakeibo.domain.repository.FixedExpenseRepository;

@Service
public class CreateFixedExpenseUseCaseImpl implements CreateFixedExpenseUseCase {
    private final FixedExpenseRepository fixedExpenseRepository;

    public CreateFixedExpenseUseCaseImpl(FixedExpenseRepository fixedExpenseRepository) {
        this.fixedExpenseRepository = fixedExpenseRepository;
    }

    @Override
    @Transactional
    public FixedExpenseResult create(CreateFixedExpenseCommand command) {
        FixedExpense fixedExpense = new FixedExpense(
            null,
            command.title(),
            command.price(),
            new MonthlyDay(command.fixedExpenseDate()),
            command.memo(),
            command.userId()
        );

        FixedExpense saved = fixedExpenseRepository.save(fixedExpense);

        return FixedExpenseResult.from(saved);
    }
}
