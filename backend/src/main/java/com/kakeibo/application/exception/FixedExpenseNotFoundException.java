package com.kakeibo.application.exception;

public class FixedExpenseNotFoundException extends RuntimeException {
    public FixedExpenseNotFoundException(String message) { super(message); }
}
