package com.kakeibo.application.usecase.asset;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.CashBalanceNotFoundException;
import com.kakeibo.domain.model.cashbalance.CashBalance;
import com.kakeibo.domain.repository.CashBalanceRepository;
import com.kakeibo.domain.repository.ExpenseRepository;
import com.kakeibo.domain.repository.IncomeRecordRepository;
import com.kakeibo.domain.repository.InvestRecordRepository;

@Service
public class GetAssetSummaryUseCaseImpl implements GetAssetSummaryUseCase {
    private final CashBalanceRepository cashBalanceRepository;
    private final IncomeRecordRepository incomeRecordRepository;
    private final ExpenseRepository expenseRepository;
    private final InvestRecordRepository investRecordRepository;

    public GetAssetSummaryUseCaseImpl(
        CashBalanceRepository cashBalanceRepository,
        IncomeRecordRepository incomeRecordRepository,
        ExpenseRepository expenseRepository,
        InvestRecordRepository investRecordRepository
    ) {
        this.cashBalanceRepository = cashBalanceRepository;
        this.incomeRecordRepository = incomeRecordRepository;
        this.expenseRepository = expenseRepository;
        this.investRecordRepository = investRecordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AssetSummaryResult get(GetAssetSummaryQuery query) {
        // 基準点の取得
        CashBalance anchor = cashBalanceRepository.findLatestByUserId(query.userId())
            .orElseThrow(() -> new CashBalanceNotFoundException("現金残高が登録されていません。"));
        
        LocalDate anchorDate = anchor.getBalanceDate();
        long B = anchor.getAmount();

        // 基準日以降の収入合計（I）
        long I = incomeRecordRepository.findAllByUserId(query.userId()).stream()
            .filter(r -> !r.getIncomeDate().isBefore(anchorDate))
            .mapToLong(r -> r.getAmount())
            .sum();
        
        // 基準日以降の支出合計（E）
        long E = expenseRepository.findAllByUserId(query.userId()).stream()
            .filter(r -> !r.getExpenseDate().isBefore(anchorDate))
            .mapToLong(r -> r.getPrice().value())
            .sum();

        // 基準日以降の投資合計（V） 
        long V = investRecordRepository.findAllByUserId(query.userId()).stream()
            .filter(r -> !r.getInvestDate().isBefore(anchorDate))
            .mapToLong(r -> r.getAmount())
            .sum();

        long cashBalance = B + I - E -V;

        // NISA元本（全期間）
        long nisaPrincipal = investRecordRepository.findAllByUserId(query.userId()).stream()
            .mapToLong(r -> r.getAmount())
            .sum();

        return new AssetSummaryResult(cashBalance, nisaPrincipal, cashBalance + nisaPrincipal);
    }
}
