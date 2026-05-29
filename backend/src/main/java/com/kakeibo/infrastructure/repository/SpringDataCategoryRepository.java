package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakeibo.infrastructure.entity.CategoryEntity;

public interface SpringDataCategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByIdAndUserId(Long id, UUID userId);
    List<CategoryEntity> findAllByUserId(UUID userId);
    boolean existsByUserIdAndName(UUID userId, String name);
}
