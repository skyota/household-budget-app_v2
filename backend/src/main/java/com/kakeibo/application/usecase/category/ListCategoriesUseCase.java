package com.kakeibo.application.usecase.category;

import java.util.List;

public interface ListCategoriesUseCase {
    List<CategoryResult> list(ListCategoriesQuery query);
}
