package com.kakeibo.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.kakeibo.application.port.PasswordHasher;
import com.kakeibo.domain.model.user.RawPassword;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(RawPassword rawPassword) {
        return encoder.encode(rawPassword.value());
    }

    @Override
    public boolean matches(RawPassword rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword.value(), hashedPassword);
    }
}
