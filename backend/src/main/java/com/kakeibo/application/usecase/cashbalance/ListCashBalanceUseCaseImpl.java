package com.kakeibo.application.usecase.cashbalance;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.usecase.shared.PaginationResult;
import com.kakeibo.domain.model.cashbalance.CashBalance;
import com.kakeibo.domain.repository.CashBalanceRepository;

@Service
public class ListCashBalanceUseCaseImpl implements ListCashBalanceUseCase {
    private final CashBalanceRepository cashBalanceRepository;

    public ListCashBalanceUseCaseImpl(CashBalanceRepository cashBalanceRepository) {
        this.cashBalanceRepository = cashBalanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ListCashBalanceResult list(ListCashBalanceQuery query) {
        List<CashBalance> all = cashBalanceRepository.findAllByUserId(query.userId());

        long totalCount = all.size();
        int totalPages = (int) Math.ceil((double) totalCount / query.perPage());

        List<CashBalanceResult> items = all.stream()
            .sorted(Comparator.comparing(CashBalance::getBalanceDate).reversed())
            .skip((long) (query.page() - 1) * query.perPage())
            .limit(query.perPage())
            .map(CashBalanceResult::from)
            .toList();

        return new ListCashBalanceResult(
            items,
            new PaginationResult(query.page(), query.perPage(), totalCount, totalPages)
        );
    }
}
