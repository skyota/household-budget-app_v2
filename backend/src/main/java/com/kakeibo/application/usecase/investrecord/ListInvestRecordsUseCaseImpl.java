package com.kakeibo.application.usecase.investrecord;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.usecase.shared.PaginationResult;
import com.kakeibo.domain.model.invest.InvestRecord;
import com.kakeibo.domain.repository.InvestRecordRepository;

@Service
public class ListInvestRecordsUseCaseImpl implements ListInvestRecordsUseCase {
    private final InvestRecordRepository investRecordRepository;

    public ListInvestRecordsUseCaseImpl(InvestRecordRepository investRecordRepository) {
        this.investRecordRepository = investRecordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ListInvestRecordsResult list(ListInvestRecordsQuery query) {
        List<InvestRecord> all = investRecordRepository.findAllByUserId(query.userId());

        long totalCount = all.size();
        int totalPages = (int) Math.ceil((double) totalCount / query.perPage());

        List<InvestRecordResult> items = all.stream()
            .sorted(Comparator.comparing(InvestRecord::getInvestDate).reversed())
            .skip((long) (query.page() - 1) * query.perPage())
            .limit(query.perPage())
            .map(InvestRecordResult::from)
            .toList();
        
        return new ListInvestRecordsResult(
            items,
            new PaginationResult(query.page(), query.perPage(), totalCount, totalPages)
        );
    }
}
