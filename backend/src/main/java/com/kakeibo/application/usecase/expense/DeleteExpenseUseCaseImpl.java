package com.kakeibo.application.usecase.expense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.ExpenseNotFoundException;
import com.kakeibo.domain.model.expense.Expense;
import com.kakeibo.domain.repository.ExpenseRepository;

@Service
public class DeleteExpenseUseCaseImpl implements DeleteExpenseUseCase {
    private final ExpenseRepository expenseRepository;

    public DeleteExpenseUseCaseImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteExpenseCommand command) {
        // findByAndUserIdで所有者チェックも兼ねる
        Expense expense = expenseRepository.findByIdAndUserId(command.expenseId(), command.userId())
            .orElseThrow(() -> new ExpenseNotFoundException("支出が見つかりません。"));

        expenseRepository.delete(expense);
    }
}
