package com.kakeibo.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.category.Category;

public interface CategoryRepository {
    Optional<Category> findByIdAndUserId(Long categoryId, UUID userId);
    List<Category> findAllByUserId(UUID userId);
    Optional<Category> findSystemCategoryByUserId(UUID userId);
    Category save(Category category);
    boolean existsByUserIdAndName(UUID userId, String name); // 同じユーザー内でカテゴリー名が重複していないか確認する
    void delete(Category category);
}
