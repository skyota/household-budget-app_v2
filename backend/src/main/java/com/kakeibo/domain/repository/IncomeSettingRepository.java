package com.kakeibo.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.income.IncomeSetting;

public interface IncomeSettingRepository {
    Optional<IncomeSetting> findByIdAndUserId(Long id, UUID userId);
    List<IncomeSetting> findAllByUserId(UUID userId);
    List<IncomeSetting> findAll();
    IncomeSetting save(IncomeSetting incomeSetting);
    void delete(IncomeSetting incomeSetting);
}
