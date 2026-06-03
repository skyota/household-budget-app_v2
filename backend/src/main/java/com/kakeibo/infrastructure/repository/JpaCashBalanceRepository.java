package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kakeibo.domain.model.cashbalance.CashBalance;
import com.kakeibo.domain.repository.CashBalanceRepository;
import com.kakeibo.infrastructure.entity.CashBalanceEntity;

@Repository
public class JpaCashBalanceRepository implements CashBalanceRepository {
    private final SpringDataCashBalanceRepository springDataCashBalanceRepository;

    public JpaCashBalanceRepository(SpringDataCashBalanceRepository springDataCashBalanceRepository) {
        this.springDataCashBalanceRepository = springDataCashBalanceRepository;
    }

    @Override
    public Optional<CashBalance> findByIdAndUserId(Long id, UUID userId) {
        return springDataCashBalanceRepository.findByIdAndUserId(id, userId)
            .map(CashBalanceEntity::toModel);
    }

    @Override
    public Optional<CashBalance> findLatestByUserId(UUID userId) {
        return springDataCashBalanceRepository.findTopByUserIdOrderByBalanceDateDesc(userId)
            .map(CashBalanceEntity::toModel);
    }

    @Override
    public List<CashBalance> findAllByUserId(UUID userId) {
        return springDataCashBalanceRepository.findAllByUserId(userId).stream()
            .map(CashBalanceEntity::toModel)
            .toList();
    }

    @Override
    public CashBalance save(CashBalance cashBalance) {
        return springDataCashBalanceRepository.save(
            CashBalanceEntity.fromModel(cashBalance)
        ).toModel();
    }

    @Override
    public void delete(CashBalance cashBalance) {
        springDataCashBalanceRepository.deleteById(cashBalance.getId());
    }
}
