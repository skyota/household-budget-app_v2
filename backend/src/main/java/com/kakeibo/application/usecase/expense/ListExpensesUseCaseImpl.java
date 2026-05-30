package com.kakeibo.application.usecase.expense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.port.ExpenseQueryService;

@Service
public class ListExpensesUseCaseImpl implements ListExpensesUseCase {
    private final ExpenseQueryService expenseQueryService;

    public ListExpensesUseCaseImpl(ExpenseQueryService expenseQueryService) {
        this.expenseQueryService = expenseQueryService;
    }

    @Override
    @Transactional(readOnly = true)
    public ListExpensesResult list(ListExpensesQuery query) {
        return expenseQueryService.findByUserId(
            query.userId(),
            query.page(),
            query.perPage()
        );
    }
}
