package com.kakeibo.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.kakeibo.domain.model.user.User;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
    Optional<User> findByApiToken(String apiToken);
    User save(User user);
    boolean existsByUsername(String username);
}
