package com.kakeibo.infrastructure.entity;

import java.util.UUID;

import com.kakeibo.domain.model.category.BudgetAmount;
import com.kakeibo.domain.model.category.Category;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long budgetAmount;
    private UUID userId;

    public static CategoryEntity fromModel(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(category.getId());
        entity.setName(category.getName());
        entity.setBudgetAmount(category.getBudgetAmount().value());
        entity.setUserId(category.getUserId());
        return entity;
    }

    public Category toModel() {
        return new Category(id, name, new BudgetAmount(budgetAmount), userId);
    }
}
