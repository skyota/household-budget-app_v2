package com.kakeibo.application.usecase.fixedexpense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.FixedExpenseNotFoundException;
import com.kakeibo.domain.model.fixedexpense.FixedExpense;
import com.kakeibo.domain.repository.FixedExpenseRepository;

@Service
public class DeleteFixedExpenseUseCaseImpl implements DeleteFixedExpenseUseCase {
    private final FixedExpenseRepository fixedExpenseRepository;

    public DeleteFixedExpenseUseCaseImpl(FixedExpenseRepository fixedExpenseRepository) {
        this.fixedExpenseRepository = fixedExpenseRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteFixedExpenseCommand command) {
        FixedExpense fixedExpense = fixedExpenseRepository.findByIdAndUserId(command.fixedExpenseId(), command.userId())
            .orElseThrow(() -> new FixedExpenseNotFoundException("固定費が見つかりません。"));

        fixedExpenseRepository.delete(fixedExpense);
    }
}
