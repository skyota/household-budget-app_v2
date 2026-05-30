package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakeibo.infrastructure.entity.ExpenseEntity;

public interface SpringDataExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    Optional<ExpenseEntity> findByIdAndUserId(Long id, UUID userId);
    List<ExpenseEntity> findAllByUserId(UUID userId);
}
