package com.kakeibo.application.usecase.expense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.CategoryNotFoundException;
import com.kakeibo.application.exception.ExpenseNotFoundException;
import com.kakeibo.domain.model.expense.Expense;
import com.kakeibo.domain.model.expense.Price;
import com.kakeibo.domain.repository.CategoryRepository;
import com.kakeibo.domain.repository.ExpenseRepository;

@Service
public class UpdateExpenseUseCaseImpl implements UpdateExpenseUseCase {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public UpdateExpenseUseCaseImpl(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public UpdateExpenseResult update(UpdateExpenseCommand command) {
        Expense expense = expenseRepository.findByIdAndUserId(command.expenseId(), command.userId())
        .orElseThrow(() -> new ExpenseNotFoundException("支出が見つかりません。"));
        
        if (command.categoryId() != null) {
            categoryRepository.findByIdAndUserId(command.categoryId(), command.userId())
                .orElseThrow(() -> new CategoryNotFoundException("カテゴリーが見つかりません。"));
        }

        // 同じIDで新しいExpenseを作成
        Expense updated = new Expense(
            expense.getId(),
            command.title(),
            new Price(command.price()),
            command.expenseDate(),
            command.categoryId(),
            command.memo(),
            expense.getUserId()
        );

        Expense saved = expenseRepository.save(updated);

        return new UpdateExpenseResult(
            saved.getId(),
            saved.getTitle(),
            saved.getPrice().value(),
            saved.getExpenseDate(),
            saved.getCategoryId(),
            saved.getMemo()
        );
    }
}
