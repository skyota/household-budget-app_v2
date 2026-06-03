package com.kakeibo.application.usecase.incomerecord;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.usecase.shared.PaginationResult;
import com.kakeibo.domain.model.income.IncomeRecord;
import com.kakeibo.domain.repository.IncomeRecordRepository;

@Service
public class ListIncomeRecordsUseCaseImpl implements ListIncomeRecordsUseCase {
    private final IncomeRecordRepository incomeRecordRepository;

    public ListIncomeRecordsUseCaseImpl(IncomeRecordRepository incomeRecordRepository) {
        this.incomeRecordRepository = incomeRecordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ListIncomeRecordsResult list(ListIncomeRecordsQuery query) {
        List<IncomeRecord> all = incomeRecordRepository.findAllByUserId(query.userId());

        long totalCount = all.size();
        int totalPages = (int) Math.ceil((double) totalCount / query.perPage());

        List<IncomeRecordResult> items = all.stream()
            .sorted(Comparator.comparing(IncomeRecord::getIncomeDate).reversed())
            .skip((long) (query.page() - 1) * query.perPage())
            .limit(query.perPage())
            .map(IncomeRecordResult::from)
            .toList();

        return new ListIncomeRecordsResult(
            items,
            new PaginationResult(query.page(), query.perPage(), totalCount, totalPages)
        );
    }
}
