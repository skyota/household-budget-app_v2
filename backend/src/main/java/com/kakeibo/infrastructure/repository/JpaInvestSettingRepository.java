package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kakeibo.domain.model.invest.InvestSetting;
import com.kakeibo.domain.repository.InvestSettingRepository;
import com.kakeibo.infrastructure.entity.InvestSettingEntity;

@Repository
public class JpaInvestSettingRepository implements InvestSettingRepository {
    private final SpringDataInvestSettingRepository springDataInvestSettingRepository;

    public JpaInvestSettingRepository(SpringDataInvestSettingRepository springDataInvestSettingRepository) {
        this.springDataInvestSettingRepository = springDataInvestSettingRepository;
    }

    @Override
    public Optional<InvestSetting> findByUserId(UUID userId) {
        return springDataInvestSettingRepository.findByUserId(userId)
            .map(InvestSettingEntity::toModel);
    }

    @Override
    public List<InvestSetting> findAll() {
        return springDataInvestSettingRepository.findAll().stream()
            .map(InvestSettingEntity::toModel)
            .toList();
    }

    @Override
    public InvestSetting save(InvestSetting investSetting) {
        return springDataInvestSettingRepository.save(
            InvestSettingEntity.fromModel(investSetting)
        ).toModel();
    }
}
