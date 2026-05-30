package com.kakeibo.infrastructure.query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.port.ExpenseQueryService;
import com.kakeibo.application.usecase.expense.ExpenseItemResult;
import com.kakeibo.application.usecase.expense.ListExpensesResult;
import com.kakeibo.application.usecase.expense.PaginationResult;

import jakarta.persistence.EntityManager;

@Service
public class JpaExpenseQueryService implements ExpenseQueryService {
    private final EntityManager em;
    // EntityManager：JPAが提供するDB操作の基本ツール
    // Spring Data JPA(JpaRepository)は、EntityManagerを内部で使って動いている
    // EntityManagerを直接使うと、JPQLを自由に書けるため、複雑なクエリに対応できる（createQueryを使う）
    public JpaExpenseQueryService(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional(readOnly = true) // EntityManager はトランザクションが自分で管理されないため、@Transactionalを明示的につける
    public ListExpensesResult findByUserId(UUID userId, int page, int perPage) {
        // ① 総件数クエリ
        long total = em.createQuery(
            "SELECT COUNT(e) FROM ExpenseEntity e WHERE e.userId = :userId",
            Long.class
        ).setParameter("userId", userId).getSingleResult();

        // ② データクエリ（LEFT JOINでカテゴリー名を取得）
        // 複数カラムをSELECTするため、1行分のデータがObjectの配列（Object[]）になる
        // rowsは「Object[]のList」 = 全行分のデータ
        List<Object[]> rows = em.createQuery("""
            SELECT e.id, e.title, e.price, e.expenseDate, e.categoryId, c.name, e.memo
            FROM ExpenseEntity e
            LEFT JOIN CategoryEntity c ON c.id = e.categoryId
            WHERE e.userId = :userId
            ORDER BY e.expenseDate DESC, e.id DESC
            """, Object[].class)
            .setParameter("userId", userId) // :userIdプレースホルダーに実際の値をセットする
            .setFirstResult((page - 1) * perPage) // 取得開始位置（オフセット）を指定する：page=1なら0件目から、page=2ならperPage件目から取得する
            .setMaxResults(perPage) // 1ページで取得する最大件数を指定する
            .getResultList(); // クエリを実行して結果をリストで返す

        // ③ Object[] → ExpenseItemResultに変換
        List<ExpenseItemResult> items = rows.stream()
            .map(row -> new ExpenseItemResult(
                // キャストをする：この値は〇〇型だと明示する → 中身は正しい型で入っているのに、JavaにはObjectとしか見えていない
                // Object型からそれぞれの型に変換
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
        int totalPages =
            total == 0
                ? 0 // 支出が0件のときはページ数も0（0除算を避けるため）
                : (int) Math.ceil( // 小数点以下を切り上げてint型に変換する
                    (double) total / perPage);
                    // totalをdouble型にキャストしてから割る（そのまま割ると小数点が切り捨てられるため）
                    // 例：total=21, perPage=20の時
                    // (double) 21 / 20 → 1.05
                    // Math.ceil(1.05) → 2.0
                    // (int) 2.0 → 2（2ページ必要）
        
        return new ListExpensesResult(
            items,
            new PaginationResult(page, perPage, total, totalPages)
        );
    }
}
