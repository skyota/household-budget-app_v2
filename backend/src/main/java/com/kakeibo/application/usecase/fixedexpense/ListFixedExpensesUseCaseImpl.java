package com.kakeibo.application.usecase.fixedexpense;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.usecase.shared.PaginationResult;
import com.kakeibo.domain.model.fixedexpense.FixedExpense;
import com.kakeibo.domain.repository.FixedExpenseRepository;

@Service
public class ListFixedExpensesUseCaseImpl implements ListFixedExpensesUseCase {
    private final FixedExpenseRepository fixedExpenseRepository;

    public ListFixedExpensesUseCaseImpl(FixedExpenseRepository fixedExpenseRepository) {
        this.fixedExpenseRepository = fixedExpenseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ListFixedExpensesResult list(ListFixedExpensesQuery query) {
        List<FixedExpense> all = fixedExpenseRepository.findAllByUserId(query.userId());

        long totalCount = all.size();
        int totalPages = (int) Math.ceil((double) totalCount / query.perPage());

        List<FixedExpenseResult> items = all.stream()
            .sorted(Comparator.comparingInt(fe -> fe.getMonthlyDay().value()))
            .skip((long) (query.page() - 1) * query.perPage())
            .limit(query.perPage())
            .map(FixedExpenseResult::from)
            .toList();
        
        return new ListFixedExpensesResult(
            items,
            new PaginationResult(query.page(), query.perPage(), totalCount, totalPages)
        );
    }
}
