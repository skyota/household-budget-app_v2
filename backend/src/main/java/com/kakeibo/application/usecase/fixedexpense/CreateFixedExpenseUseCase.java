package com.kakeibo.application.usecase.fixedexpense;

public interface CreateFixedExpenseUseCase {
    FixedExpenseResult create(CreateFixedExpenseCommand command);
}
