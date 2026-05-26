package com.kakeibo.application.exception;

public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException(String message) {
        super(message); // 親クラス（RuntimeException）のコンストラクタを呼び、渡したメッセージがRuntimeExceptionの内部に保存される
        // 後から、exception.getMessage()で取り出せるのは、super(message)で親に渡しているからである
    }
}
