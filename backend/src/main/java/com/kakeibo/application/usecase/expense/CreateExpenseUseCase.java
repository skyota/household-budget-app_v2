package com.kakeibo.application.usecase.expense;

public interface CreateExpenseUseCase {
    CreateExpenseResult create(CreateExpenseCommand command);
}
