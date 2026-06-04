package com.kakeibo.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.invest.InvestRecord;

public interface InvestRecordRepository {
    Optional<InvestRecord> findByIdAndUserId(Long id, UUID userId);
    List<InvestRecord> findAllByUserId(UUID userId);
    InvestRecord save(InvestRecord investRecord);
    void delete(InvestRecord investRecord);
}
