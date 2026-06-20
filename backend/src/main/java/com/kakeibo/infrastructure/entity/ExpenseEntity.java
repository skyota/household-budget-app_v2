package com.kakeibo.infrastructure.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.kakeibo.domain.model.expense.Expense;
import com.kakeibo.domain.model.expense.Price;

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
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private long price;
    private LocalDate expenseDate;
    private Long categoryId;
    private String memo;
    private UUID userId;
    private Long fixedExpenseId;
    private boolean isFixed;

    public static ExpenseEntity fromModel(Expense expense) {
        // staticメソッドを使えば、インスタンスを作らなくてもクラス名で直接呼ぶことができる
        // インスタンスを作る：new ExpenseEntity();
        ExpenseEntity entity = new ExpenseEntity();
        entity.setId(expense.getId());
        entity.setTitle(expense.getTitle());
        entity.setPrice(expense.getPrice().value());
        entity.setExpenseDate(expense.getExpenseDate());
        entity.setCategoryId(expense.getCategoryId());
        entity.setMemo(expense.getMemo());
        entity.setUserId(expense.getUserId());
        entity.setFixedExpenseId(expense.getFixedExpenseId());
        entity.setFixed(expense.getFixedExpenseId() != null);
        return entity;
    }

    public Expense toModel() {
        return new Expense(id, title, new Price(price), expenseDate, categoryId, memo, userId, fixedExpenseId);
    }
}
