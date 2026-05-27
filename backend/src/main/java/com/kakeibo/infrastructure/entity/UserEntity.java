package com.kakeibo.infrastructure.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.kakeibo.domain.model.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPAが要求するno-argsコンストラクタ → 外から呼ばれないようにprotectedにする
public class UserEntity {
    @Id
    private UUID id;
    private String username;
    private String hashedPassword;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static UserEntity fromModel(User user) {
        UserEntity entity = new UserEntity(); // JPA用の空インスタンスを作る
        // @NoArgsConstructor しか付けていないため、使えるコンストラクタは引数なしのものだけ
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setHashedPassword(user.getHashedPassword());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }

    public User toModel() {
        return new User(id, username, hashedPassword, createdAt, updatedAt);
    }
}
