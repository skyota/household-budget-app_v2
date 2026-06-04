package com.kakeibo.application.usecase.fixedexpense;

import com.kakeibo.domain.model.fixedexpense.FixedExpense;

public record FixedExpenseResult(
    Long id,
    String title,
    long price,
    int fixedExpenseDate,
    String memo
) {
    public static FixedExpenseResult from(FixedExpense fe) {
        return new FixedExpenseResult(
            fe.getId(),
            fe.getTitle(),
            fe.getPrice(),
            fe.getMonthlyDay().value(),
            fe.getMemo()
        );
    }
}
