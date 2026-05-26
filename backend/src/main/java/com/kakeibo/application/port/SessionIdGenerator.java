package com.kakeibo.application.port;

import com.kakeibo.domain.model.user.SessionId;

public interface SessionIdGenerator {
    SessionId generate();
}
