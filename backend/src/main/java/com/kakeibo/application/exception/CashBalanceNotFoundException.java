package com.kakeibo.application.exception;

public class CashBalanceNotFoundException extends RuntimeException {
    public CashBalanceNotFoundException(String message) { super(message); }
}
