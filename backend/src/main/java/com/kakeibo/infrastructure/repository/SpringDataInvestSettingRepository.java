package com.kakeibo.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakeibo.infrastructure.entity.InvestSettingEntity;

public interface SpringDataInvestSettingRepository extends JpaRepository<InvestSettingEntity, UUID> {
    Optional<InvestSettingEntity> findByUserId(UUID userId);
}
