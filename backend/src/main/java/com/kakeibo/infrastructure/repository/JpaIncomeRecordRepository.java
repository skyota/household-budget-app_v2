package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kakeibo.domain.model.income.IncomeRecord;
import com.kakeibo.domain.repository.IncomeRecordRepository;
import com.kakeibo.infrastructure.entity.IncomeRecordEntity;

@Repository
public class JpaIncomeRecordRepository implements IncomeRecordRepository {
    private final SpringDataIncomeRecordRepository springDataIncomeRecordRepository;

    public JpaIncomeRecordRepository(SpringDataIncomeRecordRepository springDataIncomeRecordRepository) {
        this.springDataIncomeRecordRepository = springDataIncomeRecordRepository;
    }

    @Override
    public Optional<IncomeRecord> findByIdAndUserId(Long id, UUID userId) {
        return springDataIncomeRecordRepository.findByIdAndUserId(id, userId)
            .map(IncomeRecordEntity::toModel);
    }

    @Override
    public List<IncomeRecord> findAllByUserId(UUID userId) {
        return springDataIncomeRecordRepository.findAllByUserId(userId).stream()
            .map(IncomeRecordEntity::toModel)
            .toList();
    }

    @Override
    public IncomeRecord save(IncomeRecord incomeRecord) {
        return springDataIncomeRecordRepository.save(
            IncomeRecordEntity.fromModel(incomeRecord)
        ).toModel();
    }

    @Override
    public void delete(IncomeRecord incomeRecord) {
        springDataIncomeRecordRepository.deleteById(incomeRecord.getId());
    }
}
