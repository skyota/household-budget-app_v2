package com.kakeibo.domain.model.shared;

import java.time.LocalDate;
import java.time.YearMonth;

import com.kakeibo.domain.exception.DomainValidationException;

public record MonthlyDay(int value) {
    public MonthlyDay {
        if (value < 1 || value > 31) {
            throw new DomainValidationException("日付は1~31の間で設定してください。");
        }
    }

    // 月末丸め込みロジック（31と設定したとき、2月なら28にする）
    public LocalDate resolveActualDate(YearMonth month) {
        int day = Math.min(value, month.lengthOfMonth());
        return month.atDay(day);
    }
}
