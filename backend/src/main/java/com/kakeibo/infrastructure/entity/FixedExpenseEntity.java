package com.kakeibo.infrastructure.entity;

import java.util.UUID;

import com.kakeibo.domain.model.fixedexpense.FixedExpense;
import com.kakeibo.domain.model.shared.MonthlyDay;

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
@Table(name = "fixed_expenses")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FixedExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private long price;
    private int fixedExpenseDate;
    private String memo;
    private UUID userId;

    public static FixedExpenseEntity fromModel(FixedExpense fixedExpense) {
        FixedExpenseEntity entity = new FixedExpenseEntity();
        entity.setId(fixedExpense.getId());
        entity.setTitle(fixedExpense.getTitle());
        entity.setPrice(fixedExpense.getPrice());
        entity.setFixedExpenseDate(fixedExpense.getMonthlyDay().value());
        entity.setMemo(fixedExpense.getMemo());
        entity.setUserId(fixedExpense.getUserId());
        return entity;
    }

    public FixedExpense toModel() {
        return new FixedExpense(id, title, price, new MonthlyDay(fixedExpenseDate), memo, userId);
    }
}
