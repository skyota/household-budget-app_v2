package com.kakeibo.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kakeibo.infrastructure.entity.IncomeRecordEntity;

public interface SpringDataIncomeRecordRepository extends JpaRepository<IncomeRecordEntity, Long> {
    Optional<IncomeRecordEntity> findByIdAndUserId(Long id, UUID userId);
    List<IncomeRecordEntity> findAllByUserId(UUID userId);

    @Query("SELECT COUNT(e) > 0 FROM IncomeRecordEntity e WHERE e.userId = :userId AND e.isRegular = true AND e.title = :title AND e.incomeDate BETWEEN :startDate AND :endDate")
    boolean existsRegularIncomeByUserIdAndTitleAndIncomeDateBetween(@Param("userId") UUID userId, @Param("title") String title, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
