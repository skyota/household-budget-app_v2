package com.kakeibo.application.usecase.fixedexpense;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.model.category.BudgetAmount;
import com.kakeibo.domain.model.category.Category;
import com.kakeibo.domain.model.expense.Expense;
import com.kakeibo.domain.model.expense.Price;
import com.kakeibo.domain.model.fixedexpense.FixedExpense;
import com.kakeibo.domain.repository.CategoryRepository;
import com.kakeibo.domain.repository.ExpenseRepository;
import com.kakeibo.domain.repository.FixedExpenseRepository;

@Service
public class AutoGenerateFixedExpensesUseCaseImpl implements AutoGenerateFixedExpensesUseCase {

    private static final String SYSTEM_CATEGORY_NAME = "固定費";

    private final FixedExpenseRepository fixedExpenseRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public AutoGenerateFixedExpensesUseCaseImpl(
            FixedExpenseRepository fixedExpenseRepository,
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository) {
        this.fixedExpenseRepository = fixedExpenseRepository;
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void execute() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        List<FixedExpense> allFixedExpenses = fixedExpenseRepository.findAll();
        if (allFixedExpenses.isEmpty()) {
            return;
        }

        Map<UUID, List<FixedExpense>> byUser = allFixedExpenses.stream()
                .collect(Collectors.groupingBy(FixedExpense::getUserId));

        for (Map.Entry<UUID, List<FixedExpense>> entry : byUser.entrySet()) {
            UUID userId = entry.getKey();
            List<FixedExpense> userFixedExpenses = entry.getValue();

            Category systemCategory = findOrCreateSystemCategory(userId);

            for (FixedExpense fe : userFixedExpenses) {
                LocalDate paymentDate = fe.getMonthlyDay().resolveActualDate(currentMonth);

                if (!paymentDate.isAfter(today)) {
                    boolean alreadyCreated = expenseRepository.existsByFixedExpenseIdAndExpenseDateBetween(
                            fe.getId(), startOfMonth, endOfMonth);

                    if (!alreadyCreated) {
                        Expense expense = new Expense(
                                null,
                                fe.getTitle(),
                                new Price(fe.getPrice()),
                                paymentDate,
                                systemCategory.getId(),
                                null,
                                userId,
                                fe.getId());
                        expenseRepository.save(expense);
                    }
                }
            }
        }
    }

    private Category findOrCreateSystemCategory(UUID userId) {
        return categoryRepository.findSystemCategoryByUserId(userId)
                .orElseGet(() -> {
                    Category newCategory = new Category(
                            null,
                            SYSTEM_CATEGORY_NAME,
                            new BudgetAmount(0),
                            userId,
                            true);
                    return categoryRepository.save(newCategory);
                });
    }
}
