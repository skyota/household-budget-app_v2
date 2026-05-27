package com.kakeibo.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakeibo.infrastructure.entity.LoginSessionEntity;

public interface SpringDataLoginSessionRepository extends JpaRepository<LoginSessionEntity, String> {
    // 基本的なCRUDはJpaRepositoryから継承するだけでOK
}
