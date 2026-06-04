package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kakeibo.domain.model.invest.InvestRecord;
import com.kakeibo.domain.repository.InvestRecordRepository;
import com.kakeibo.infrastructure.entity.InvestRecordEntity;

@Repository
public class JpaInvestRecordRepository implements InvestRecordRepository {
    private final SpringDataInvestRecordRepository springDataInvestRecordRepository;

    public JpaInvestRecordRepository(SpringDataInvestRecordRepository springDataInvestRecordRepository) {
        this.springDataInvestRecordRepository = springDataInvestRecordRepository;
    }

    @Override
    public Optional<InvestRecord> findByIdAndUserId(Long id, UUID userId) {
        return springDataInvestRecordRepository.findByIdAndUserId(id, userId)
            .map(InvestRecordEntity::toModel);
    }

    @Override
    public List<InvestRecord> findAllByUserId(UUID userId) {
        return springDataInvestRecordRepository.findAllByUserId(userId).stream()
            .map(InvestRecordEntity::toModel)
            .toList();
    }

    @Override
    public InvestRecord save(InvestRecord investRecord) {
        return springDataInvestRecordRepository.save(
            InvestRecordEntity.fromModel(investRecord)
        ).toModel();
    }

    @Override
    public void delete(InvestRecord investRecord) {
        springDataInvestRecordRepository.deleteById(investRecord.getId());
    }
}
