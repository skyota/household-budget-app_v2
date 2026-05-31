package com.kakeibo.application.usecase.auth;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.HexFormat;
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
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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

        // 32バイトのランダムなバイト列を生成し16進数文字列に変換（64文字）
        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);
        String apiToken = HexFormat.of().formatHex(tokenBytes);

        // Userエンティティを生成
        OffsetDateTime now = OffsetDateTime.now();
        User user = new User(UUID.randomUUID(), command.username(), hashedPassword, apiToken, now, now);

        // 保存
        User saved = userRepository.save(user);

        // Presentation層に返す結果を組み立てる
        return new RegisterUserResult(saved.getId(), saved.getUsername());
    }
}
