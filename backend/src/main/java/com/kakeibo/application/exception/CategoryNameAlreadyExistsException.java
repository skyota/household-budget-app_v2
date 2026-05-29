package com.kakeibo.application.exception;

public class CategoryNameAlreadyExistsException extends RuntimeException {
    public CategoryNameAlreadyExistsException(String message) {
        super(message);
    }
}
