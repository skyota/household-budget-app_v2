package com.kakeibo.application.usecase.auth;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakeibo.application.exception.InvalidCredentialsException;
import com.kakeibo.application.port.PasswordHasher;
import com.kakeibo.application.port.SessionIdGenerator;
import com.kakeibo.domain.model.user.LoginSession;
import com.kakeibo.domain.model.user.RawPassword;
import com.kakeibo.domain.model.user.SessionId;
import com.kakeibo.domain.model.user.User;
import com.kakeibo.domain.repository.LoginSessionRepository;
import com.kakeibo.domain.repository.UserRepository;

@Service
public class LoginUseCaseImpl implements LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final SessionIdGenerator sessionIdGenerator;
    private final LoginSessionRepository loginSessionRepository;

    public LoginUseCaseImpl(UserRepository userRepository, PasswordHasher passwordHasher, SessionIdGenerator sessionIdGenerator, LoginSessionRepository loginSessionRepository) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.sessionIdGenerator = sessionIdGenerator;
        this.loginSessionRepository = loginSessionRepository;
    }
    
    @Override
    @Transactional
    public LoginResult login(LoginCommand command) {
        // ユーザーを取得
        User user = userRepository.findByUsername(command.username())
            .orElseThrow(() -> new InvalidCredentialsException("ユーザー名またはパスワードが正しくありません。"));

        // パスワード照合
        RawPassword rawPassword = new RawPassword(command.password());
        if (!passwordHasher.matches(rawPassword, user.getHashedPassword())) {
            throw new InvalidCredentialsException("ユーザー名またはパスワードが正しくありません。");
        }

        // セッションIDを生成
        SessionId sessionId = sessionIdGenerator.generate();

        // LoginSessionエンティティを生成
        OffsetDateTime now = OffsetDateTime.now();
        LoginSession session = new LoginSession(
            sessionId,
            user.getId(),
            now.plusDays(30),
            now,
            now,
            null // revokedAt（まだ失効していない）
        );

        // 保存
        loginSessionRepository.save(session);

        // セッションIDの文字列値を返す（Cookieに返すため）
        return new LoginResult(sessionId.value(), user.getId(), user.getUsername());
    }
}
