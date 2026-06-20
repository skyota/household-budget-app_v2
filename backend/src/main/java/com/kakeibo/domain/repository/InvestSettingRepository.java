package com.kakeibo.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.invest.InvestSetting;

public interface InvestSettingRepository {
    Optional<InvestSetting> findByUserId(UUID userId);
    List<InvestSetting> findAll();
    InvestSetting save(InvestSetting investSetting);
}
