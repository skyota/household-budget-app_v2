package com.kakeibo.application.usecase.incomesetting;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.usecase.shared.PaginationResult;
import com.kakeibo.domain.model.income.IncomeSetting;
import com.kakeibo.domain.repository.IncomeSettingRepository;

@Service
public class ListIncomeSettingsUseCaseImpl implements ListIncomeSettingsUseCase {
    private final IncomeSettingRepository incomeSettingRepository;

    public ListIncomeSettingsUseCaseImpl(IncomeSettingRepository incomeSettingRepository) {
        this.incomeSettingRepository = incomeSettingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ListIncomeSettingsResult list(ListIncomeSettingsQuery query) {
        List<IncomeSetting> all = incomeSettingRepository.findAllByUserId(query.userId());

        long totalCount = all.size();
        int totalPages = (int) Math.ceil((double) totalCount / query.perPage());

        List<IncomeSettingResult> items = all.stream()
            .sorted(Comparator.comparingInt(s -> s.getIncomeDate().value()))
            .skip((long) (query.page() - 1) * query.perPage())
            .limit(query.perPage())
            .map(IncomeSettingResult::from)
            .toList();

        return new ListIncomeSettingsResult(
            items,
            new PaginationResult(query.page(), query.perPage(), totalCount, totalPages)
        );
    }
}
