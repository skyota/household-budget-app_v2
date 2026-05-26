package com.kakeibo.application.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message); // RuntimeExceptionのコンストラクタにメッセージを渡す
    }
}
