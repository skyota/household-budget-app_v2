package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakeibo.infrastructure.entity.FixedExpenseEntity;

public interface SpringDataFixedExpenseRepository extends JpaRepository<FixedExpenseEntity, Long> {
    Optional<FixedExpenseEntity> findByIdAndUserId(Long id, UUID userId);
    List<FixedExpenseEntity> findAllByUserId(UUID userId);
}
