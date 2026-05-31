package com.kakeibo.presentation.controller;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakeibo.application.usecase.auth.LoginCommand;
import com.kakeibo.application.usecase.auth.LoginResult;
import com.kakeibo.application.usecase.auth.LoginUseCase;
import com.kakeibo.application.usecase.auth.LogoutUseCase;
import com.kakeibo.application.usecase.auth.RegisterUserCommand;
import com.kakeibo.application.usecase.auth.RegisterUserResult;
import com.kakeibo.application.usecase.auth.RegisterUserUseCase;
import com.kakeibo.domain.model.user.User;
import com.kakeibo.domain.repository.UserRepository;
import com.kakeibo.presentation.dto.LoginRequest;
import com.kakeibo.presentation.dto.LoginResponse;
import com.kakeibo.presentation.dto.LogoutResponse;
import com.kakeibo.presentation.dto.RegisterRequest;
import com.kakeibo.presentation.dto.RegisterResponse;
import com.kakeibo.presentation.exception.UnauthorizedException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final UserRepository userRepository;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase, LogoutUseCase logoutUseCase, UserRepository userRepository) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserResult result = registerUserUseCase.register(
            new RegisterUserCommand(request.username(), request.password())
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new RegisterResponse(result.id(), result.username()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = loginUseCase.login(
            new LoginCommand(request.username(), request.password())
        );

        ResponseCookie cookie = ResponseCookie.from("session", result.sessionId())
            .path("/")
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite("Lax")
            .maxAge(Duration.ofDays(30))
            .build();
        
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new LoginResponse(result.userId(), result.username()));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
        @CookieValue(name = "session", required = false) String sessionId
    ) {
        // sessionIdがあるときは、サーバー側のセッション無効化処理をする
        if (sessionId != null) {
            logoutUseCase.logout(sessionId);
        }

        // ブラウザに削除を指示するためのCookieオブジェクト
        ResponseCookie cookie = ResponseCookie.from("session", "")
            .path("/")
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite("Lax")
            .maxAge(0) // 寿命0で
            .build();
        
        return  ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new LogoutResponse("ログアウトしました。"));
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me(
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("ユーザーが見つかりません。"));
        return ResponseEntity.ok(new LoginResponse(userId, user.getUsername()));
    }
}
