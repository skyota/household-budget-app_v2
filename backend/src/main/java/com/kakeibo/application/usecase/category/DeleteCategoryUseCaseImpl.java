package com.kakeibo.application.usecase.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.CategoryNotFoundException;
import com.kakeibo.domain.exception.DomainValidationException;
import com.kakeibo.domain.model.category.Category;
import com.kakeibo.domain.repository.CategoryRepository;

@Service
public class DeleteCategoryUseCaseImpl implements DeleteCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public DeleteCategoryUseCaseImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteCategoryCommand command) {
        // findByIdAndUserId で所有者チェックも兼ねる。
        // 他ユーザーのカテゴリーは存在しないものとして 404 を返す（IDOR対策）
        Category category = categoryRepository.findByIdAndUserId(command.categoryId(), command.userId())
            .orElseThrow(() -> new CategoryNotFoundException("カテゴリーが見つかりません。"));

        if (category.isSystem()) {
            throw new DomainValidationException("システムカテゴリーは削除できません。");
        }

        categoryRepository.delete(category);
    }
}
