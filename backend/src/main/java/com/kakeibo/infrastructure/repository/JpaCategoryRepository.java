package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kakeibo.domain.model.category.Category;
import com.kakeibo.domain.repository.CategoryRepository;
import com.kakeibo.infrastructure.entity.CategoryEntity;

@Repository
public class JpaCategoryRepository implements CategoryRepository {
    private final SpringDataCategoryRepository springDataCategoryRepository;

    public JpaCategoryRepository(SpringDataCategoryRepository springDataCategoryRepository) {
        this.springDataCategoryRepository = springDataCategoryRepository;
    }

    @Override
    public Optional<Category> findByIdAndUserId(Long categoryId, UUID userId) {
        return springDataCategoryRepository.findByIdAndUserId(categoryId, userId).map(CategoryEntity::toModel);
    }

    @Override
    public List<Category> findAllByUserId(UUID userId) {
        return springDataCategoryRepository.findAllByUserId(userId)
            .stream()
            .map(CategoryEntity::toModel)
            .toList();
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = CategoryEntity.fromModel(category);
        return springDataCategoryRepository.save(entity).toModel();
    }

    @Override
    public boolean existsByUserIdAndName(UUID userId, String name) {
        return springDataCategoryRepository.existsByUserIdAndName(userId, name);
    }

    @Override
    public void delete(Category category) {
        springDataCategoryRepository.deleteById(category.getId());
    }
}
