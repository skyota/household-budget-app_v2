package com.kakeibo.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakeibo.infrastructure.entity.IncomeSettingEntity;

public interface SpringDataIncomeSettingRepository extends JpaRepository<IncomeSettingEntity, Long> {
    Optional<IncomeSettingEntity> findByIdAndUserId(Long id, UUID userId);
    List<IncomeSettingEntity> findAllByUserId(UUID userId);
}
