package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kakeibo.domain.model.fixedexpense.FixedExpense;
import com.kakeibo.domain.repository.FixedExpenseRepository;
import com.kakeibo.infrastructure.entity.FixedExpenseEntity;

@Repository
public class JpaFixedExpenseRepository implements FixedExpenseRepository {
    private final SpringDataFixedExpenseRepository springDataFixedExpenseRepository;

    public JpaFixedExpenseRepository(SpringDataFixedExpenseRepository springDataFixedExpenseRepository) {
        this.springDataFixedExpenseRepository = springDataFixedExpenseRepository;
    }

    @Override
    public Optional<FixedExpense> findByIdAndUserId(Long id, UUID userId) {
        return springDataFixedExpenseRepository.findByIdAndUserId(id, userId)
            .map(FixedExpenseEntity::toModel);
    }

    @Override
    public List<FixedExpense> findAllByUserId(UUID userId) {
        return springDataFixedExpenseRepository.findAllByUserId(userId).stream()
            .map(FixedExpenseEntity::toModel)
            .toList();
    }

    @Override
    public FixedExpense save(FixedExpense fixedExpense) {
        return springDataFixedExpenseRepository.save(
            FixedExpenseEntity.fromModel(fixedExpense)
        ).toModel();
    }

    @Override
    public void delete(FixedExpense fixedExpense) {
        springDataFixedExpenseRepository.deleteById(fixedExpense.getId());
    }
}
