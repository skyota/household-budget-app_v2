package com.kakeibo.domain.model.user;

public record RawPassword(String value) {
    public RawPassword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("パスワードは必須です。");
        }
    
        if (value.length() < 8) {
            throw new IllegalArgumentException("パスワードは8文字以上で設定してください。");
        }
    }

    @Override
    public String toString() {
        return "RawPassword[value=***]"; // ログを絶対出さない
    }
}
