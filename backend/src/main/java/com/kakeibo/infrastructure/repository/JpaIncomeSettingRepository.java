package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kakeibo.domain.model.income.IncomeSetting;
import com.kakeibo.domain.repository.IncomeSettingRepository;
import com.kakeibo.infrastructure.entity.IncomeSettingEntity;

@Repository
public class JpaIncomeSettingRepository implements IncomeSettingRepository {
    private final SpringDataIncomeSettingRepository springDataIncomeSettingRepository;

    public JpaIncomeSettingRepository(SpringDataIncomeSettingRepository springDataIncomeSettingRepository) {
        this.springDataIncomeSettingRepository = springDataIncomeSettingRepository;
    }

    @Override
    public Optional<IncomeSetting> findByIdAndUserId(Long id, UUID userId) {
        return springDataIncomeSettingRepository.findByIdAndUserId(id, userId)
            .map(IncomeSettingEntity::toModel);
    }

    @Override
    public List<IncomeSetting> findAllByUserId(UUID userId) {
        return springDataIncomeSettingRepository.findAllByUserId(userId).stream()
            .map(IncomeSettingEntity::toModel)
            .toList();
    }

    @Override
    public IncomeSetting save(IncomeSetting incomeSetting) {
        return springDataIncomeSettingRepository.save(
            IncomeSettingEntity.fromModel(incomeSetting)
        ).toModel();
    }

    @Override
    public void delete(IncomeSetting incomeSetting) {
        springDataIncomeSettingRepository.deleteById(incomeSetting.getId());
    }
}
