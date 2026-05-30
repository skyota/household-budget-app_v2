package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kakeibo.domain.model.expense.Expense;
import com.kakeibo.domain.repository.ExpenseRepository;
import com.kakeibo.infrastructure.entity.ExpenseEntity;

@Repository
public class JpaExpenseRepository implements ExpenseRepository {
    private final SpringDataExpenseRepository springDataExpenseRepository;

    public JpaExpenseRepository(SpringDataExpenseRepository springDataExpenseRepository) {
        this.springDataExpenseRepository = springDataExpenseRepository;
    }

    @Override
    public Optional<Expense> findByIdAndUserId(Long expenseId, UUID userId) {
        return springDataExpenseRepository.findByIdAndUserId(expenseId, userId)
            .map(ExpenseEntity::toModel);
    }

    @Override
    public List<Expense> findAllByUserId(UUID userId) {
        return springDataExpenseRepository.findAllByUserId(userId).stream()
            .map(ExpenseEntity::toModel)
            .toList();
    }

    @Override
    public Expense save(Expense expense) {
        ExpenseEntity entity = ExpenseEntity.fromModel(expense);
        // ExpenseEntityではstaticメソッドが使われているので、インスタンスを作らなくてもクラス名で直接呼ぶことができる
        // インスタンスを作る：new ExpenseEntity();

        return springDataExpenseRepository.save(entity).toModel();
    }

    @Override
    public void delete(Expense expense) {
        springDataExpenseRepository.deleteById(expense.getId());
    }
}
