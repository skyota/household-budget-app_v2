package com.kakeibo.application.usecase.expense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.expense.Expense;
import com.kakeibo.domain.model.expense.Price;
import com.kakeibo.domain.repository.ExpenseRepository;

@Service
public class CreateExpenseUseCaseImpl implements CreateExpenseUseCase {
    private final ExpenseRepository expenseRepository;

    public CreateExpenseUseCaseImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    @Transactional
    public CreateExpenseResult create(CreateExpenseCommand command) {
        Expense expense = new Expense(
            null,
            command.title(),
            new Price(command.price()),
            command.expenseDate(),
            command.categoryId(),
            command.memo(),
            command.userId()
        );

        Expense saved = expenseRepository.save(expense);
        return new CreateExpenseResult(
            saved.getId(),
            saved.getTitle(),
            saved.getPrice().value(),
            saved.getExpenseDate(),
            saved.getCategoryId(),
            saved.getMemo()
        );
    }
}
