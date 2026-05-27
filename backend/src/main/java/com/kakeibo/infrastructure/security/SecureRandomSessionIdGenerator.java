package com.kakeibo.infrastructure.security;

import java.security.SecureRandom;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import com.kakeibo.application.port.SessionIdGenerator;
import com.kakeibo.domain.model.user.SessionId;

@Component
public class SecureRandomSessionIdGenerator implements SessionIdGenerator {
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public SessionId generate() {
        // byte:8ビット（0~255）の数値の一つを表す
        // byte[]:byteの配列→new byte[32]:32個のbyteを格納できる配列（32 x 8ビット = 256ビット分の入れ物。この時点の中身は全部0）
        byte[] bytes = new byte[32];

        // SecureRandomはOSレベルの乱数発生器で予測不可能なランダムな値を生成する（Randomはアルゴリズムで生成するため、パターンを解析されると予測される可能性がある）
        // nextBytes:配列の中身をランダムな値で埋める
        secureRandom.nextBytes(bytes);

        // HexFormat：変換ツールを取得
        // formatHex(bytes)：byte配列 → 16進数の文字列に変換する
        // byte の値  →  16進数2文字
        // 0         →  "00"
        // 15        →  "0f"
        // 255       →  "ff"

        // 32個の byte → それぞれ2文字 → 合計64文字の文字列になります。

        // "2a4f8c1e..." ← こういう64文字の文字列
        return new SessionId(HexFormat.of().formatHex(bytes)); // 64文字の16進数
    }
}
