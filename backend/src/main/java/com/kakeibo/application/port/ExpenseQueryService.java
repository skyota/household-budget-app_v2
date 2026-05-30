package com.kakeibo.application.port;

import java.util.UUID;

import com.kakeibo.application.usecase.expense.ListExpensesResult;

/**
 * 支出一覧取得のためのクエリサービスインターフェース（ポート）。
 *
 * このインターフェースを Application 層に置く理由：
 * - 支出一覧にはカテゴリー名（categoryName）が必要だが、
 *   Expense ドメインモデルはカテゴリー名を持っていない。
 * - カテゴリー名を取得するには JOIN クエリが必要であり、
 *   それは JPA という Infrastructure の知識を使う。
 * - Application 層は Infrastructure（JPA）に依存してはいけないため、
 *   「こういうデータが欲しい」という要求だけをインターフェースとして定義し、
 *   実装は Infrastructure 層（JpaExpenseQueryService）に任せる。
 *
 * なぜ domain/repository/ExpenseRepository に追加しないのか：
 * - ExpenseRepository はドメインモデル（Expense）を返す責務を持つ。
 * - ListExpensesResult はページネーション情報やカテゴリー名を含む
 *   Application 層の概念であり、Domain 層が知るべきでない。
 */

public interface ExpenseQueryService {
    ListExpensesResult findByUserId(UUID userId, Integer year, Integer month, int page, int perPage);
}
