package com.kakeibo.application.port;

import com.kakeibo.domain.model.user.RawPassword;

public interface PasswordHasher {
    String hash(RawPassword rawPassword);
    boolean matches(RawPassword rawPassword, String hashedPassword);
}
