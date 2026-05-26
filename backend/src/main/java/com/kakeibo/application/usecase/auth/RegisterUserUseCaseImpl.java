package com.kakeibo.application.usecase.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.UsernameAlreadyExistsException;
import com.kakeibo.application.port.PasswordHasher;
import com.kakeibo.domain.model.user.RawPassword;
import com.kakeibo.domain.model.user.User;
import com.kakeibo.domain.repository.UserRepository;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCaseImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public RegisterUserResult register(RegisterUserCommand command) {
        // ユーザー名の重複チェック
        if (userRepository.existsByUsername(command.username())) {
            throw new UsernameAlreadyExistsException("このユーザー名は既に使用されています。");
        }

        // String → RawPasswordに変換してからハッシュ化
        RawPassword rawPassword = new RawPassword(command.password());
        String hashedPassword = passwordHasher.hash(rawPassword);

        // Userエンティティを生成
        OffsetDateTime now = OffsetDateTime.now();
        User user = new User(UUID.randomUUID(), command.username(), hashedPassword, now, now);

        // 保存
        User saved = userRepository.save(user);

        // Presentation層に返す結果を組み立てる
        return new RegisterUserResult(saved.getId(), saved.getUsername());
    }
}
