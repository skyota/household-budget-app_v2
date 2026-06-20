package com.kakeibo.application.usecase.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.CategoryNameAlreadyExistsException;
import com.kakeibo.domain.model.category.BudgetAmount;
import com.kakeibo.domain.model.category.Category;
import com.kakeibo.domain.repository.CategoryRepository;

@Service
public class CreateCategoryUseCaseImpl implements CreateCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public CreateCategoryUseCaseImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResult create(CreateCategoryCommand command) {
        if (categoryRepository.existsByUserIdAndName(command.userId(), command.name())) {
            throw new CategoryNameAlreadyExistsException("このカテゴリー名はすでに使用されています。");
        }

        Category category = new Category(
            null,
            command.name(),
            new BudgetAmount(command.budgetAmount()),
            command.userId(),
            false
        );

        Category saved = categoryRepository.save(category);
        return new CategoryResult(
            saved.getId(),
            saved.getName(),
            saved.getBudgetAmount().value(),
            saved.isSystem()
        );
    }
}
