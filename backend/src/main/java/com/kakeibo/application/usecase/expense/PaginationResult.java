package com.kakeibo.application.usecase.expense;

public record PaginationResult(
    int currentPage, // 現在何ページ目か（1始まり）
    int perPage,     // 1ページに表示する件数
    long totalCount, // 支出の総件数
    int totalPages   // 総ページ数
) {}
