package com.kakeibo.application.usecase.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.domain.repository.CategoryRepository;

@Service
public class ListCategoriesUseCaseImpl implements ListCategoriesUseCase {
    private final CategoryRepository categoryRepository;

    public ListCategoriesUseCaseImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResult> list(ListCategoriesQuery query) {
        return categoryRepository.findAllByUserId(query.userId()).stream()
            .map(c -> new CategoryResult(c.getId(), c.getName(), c.getBudgetAmount().value(), c.isSystem()))
            .toList();
    }
}
