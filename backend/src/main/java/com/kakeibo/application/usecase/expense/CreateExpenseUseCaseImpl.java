package com.kakeibo.application.usecase.expense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.CategoryNotFoundException;
import com.kakeibo.domain.model.expense.Expense;
import com.kakeibo.domain.model.expense.Price;
import com.kakeibo.domain.repository.CategoryRepository;
import com.kakeibo.domain.repository.ExpenseRepository;

@Service
public class CreateExpenseUseCaseImpl implements CreateExpenseUseCase {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public CreateExpenseUseCaseImpl(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CreateExpenseResult create(CreateExpenseCommand command) {
        if (command.categoryId() != null) {
            categoryRepository.findByIdAndUserId(command.categoryId(), command.userId())
                .orElseThrow(() -> new CategoryNotFoundException("カテゴリーが見つかりません。"));
        }

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
