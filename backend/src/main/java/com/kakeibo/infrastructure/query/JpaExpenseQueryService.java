package com.kakeibo.infrastructure.query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.port.ExpenseQueryService;
import com.kakeibo.application.usecase.expense.ExpenseItemResult;
import com.kakeibo.application.usecase.expense.ListExpensesResult;
import com.kakeibo.application.usecase.shared.PaginationResult;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@Service
public class JpaExpenseQueryService implements ExpenseQueryService {
    private final EntityManager em;

    public JpaExpenseQueryService(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional(readOnly = true)
    public ListExpensesResult findByUserId(UUID userId, Integer year, Integer month, int page, int perPage) {
        boolean filterByMonth = year != null && month != null;
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (filterByMonth) {
            startDate = LocalDate.of(year, month, 1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        }

        String dateFilter = filterByMonth ? " AND e.expenseDate BETWEEN :startDate AND :endDate" : "";

        // ① 総件数クエリ
        TypedQuery<Long> countQuery = em.createQuery(
            "SELECT COUNT(e) FROM ExpenseEntity e WHERE e.userId = :userId" + dateFilter,
            Long.class
        ).setParameter("userId", userId);
        if (filterByMonth) {
            countQuery.setParameter("startDate", startDate);
            countQuery.setParameter("endDate", endDate);
        }
        long total = countQuery.getSingleResult();

        // ② データクエリ（LEFT JOINでカテゴリー名を取得）
        TypedQuery<Object[]> dataQuery = em.createQuery(
            "SELECT e.id, e.title, e.price, e.expenseDate, e.categoryId, c.name, e.memo"
            + " FROM ExpenseEntity e"
            + " LEFT JOIN CategoryEntity c ON c.id = e.categoryId"
            + " WHERE e.userId = :userId" + dateFilter
            + " ORDER BY e.expenseDate DESC, e.id DESC",
            Object[].class
        )
        .setParameter("userId", userId)
        .setFirstResult((page - 1) * perPage)
        .setMaxResults(perPage);
        if (filterByMonth) {
            dataQuery.setParameter("startDate", startDate);
            dataQuery.setParameter("endDate", endDate);
        }
        List<Object[]> rows = dataQuery.getResultList();

        // ③ Object[] → ExpenseItemResultに変換
        List<ExpenseItemResult> items = rows.stream()
            .map(row -> new ExpenseItemResult(
                (Long)      row[0],
                (String)    row[1],
                (long)      row[2],
                (LocalDate) row[3],
                (Long)      row[4],
                (String)    row[5],
                (String)    row[6]
            ))
            .toList();

        // ④ 総ページ数を計算
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / perPage);

        return new ListExpensesResult(
            items,
            new PaginationResult(page, perPage, total, totalPages)
        );
    }
}
