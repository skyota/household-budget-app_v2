package com.kakeibo.application.usecase.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.CategoryNameAlreadyExistsException;
import com.kakeibo.application.exception.CategoryNotFoundException;
import com.kakeibo.domain.model.category.BudgetAmount;
import com.kakeibo.domain.model.category.Category;
import com.kakeibo.domain.repository.CategoryRepository;

@Service
public class UpdateCategoryUseCaseImpl implements UpdateCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public UpdateCategoryUseCaseImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public UpdateCategoryResult update(UpdateCategoryCommand command) {
        Category category = categoryRepository.findByIdAndUserId(command.categoryId(), command.userId())
            .orElseThrow(() -> new CategoryNotFoundException("カテゴリーが見つかりません。"));

        // 名前が変わる場合のみ重複チェック
        if (!category.getName().equals(command.name()) && categoryRepository.existsByUserIdAndName(command.userId(), command.name())) {
            throw new CategoryNameAlreadyExistsException("このカテゴリー名は既に使用されています。");
        }

        Category updated = new Category(
            category.getId(),
            command.name(),
            new BudgetAmount(command.budgetAmount()),
            category.getUserId()
        );

        Category saved = categoryRepository.save(updated);
        return new UpdateCategoryResult(saved.getId(), saved.getName(), saved.getBudgetAmount().value());
    }
}
