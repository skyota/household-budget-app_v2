package com.kakeibo.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kakeibo.domain.model.user.User;
import com.kakeibo.domain.repository.UserRepository;
import com.kakeibo.infrastructure.entity.UserEntity;

@Repository
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository springDataUserRepository;

    public JpaUserRepository(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataUserRepository.findByUsername(username)
            .map(UserEntity::toModel); // Entity → Domainモデルに変換
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id)
            .map(UserEntity::toModel);
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.fromModel(user); // Domainモデル → Entityに変換
        return springDataUserRepository.save(entity).toModel();
    }

    @Override
    public boolean existsByUsername(String username) {
        return springDataUserRepository.existsByUsername(username);
    }
}
