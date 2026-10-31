package com.kakeibo.application.usecase.fixedexpense;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FixedExpenseHistoryService {
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Tokyo");
    private final JdbcTemplate jdbc;

    public FixedExpenseHistoryService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public void recordCurrentTotal(UUID userId) {
        YearMonth current = YearMonth.from(LocalDate.now(APP_ZONE));
        long total = currentTotal(userId);
        jdbc.update("""
            INSERT INTO fixed_expense_monthly_totals (user_id, year, month, total)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (user_id, year, month) DO UPDATE SET total = EXCLUDED.total
            """, userId, current.getYear(), current.getMonthValue(), total);
    }

    @Transactional
    public List<MonthlyTotal> listYear(UUID userId, int year) {
        YearMonth current = YearMonth.from(LocalDate.now(APP_ZONE));
        var last = jdbc.query("""
            SELECT year, month, total FROM fixed_expense_monthly_totals
            WHERE user_id = ? AND (year < ? OR (year = ? AND month <= ?))
            ORDER BY year DESC, month DESC LIMIT 1
            """, (rs, row) -> new MonthlyTotal(rs.getInt("year"), rs.getInt("month"), rs.getLong("total")),
            userId, current.getYear(), current.getYear(), current.getMonthValue());

        if (last.isEmpty()) {
            // Start the fixed expense history from the first month this feature is used.
            recordCurrentTotal(userId);
        } else {
            YearMonth cursor = YearMonth.of(last.get(0).year(), last.get(0).month()).plusMonths(1);
            long carry = last.get(0).total();
            while (!cursor.isAfter(current)) {
                jdbc.update("""
                    INSERT INTO fixed_expense_monthly_totals (user_id, year, month, total)
                    VALUES (?, ?, ?, ?) ON CONFLICT (user_id, year, month) DO NOTHING
                    """, userId, cursor.getYear(), cursor.getMonthValue(), carry);
                cursor = cursor.plusMonths(1);
            }
        }

        return jdbc.query("""
            SELECT year, month, total FROM fixed_expense_monthly_totals
            WHERE user_id = ? AND year = ? ORDER BY month
            """, (rs, row) -> new MonthlyTotal(rs.getInt("year"), rs.getInt("month"), rs.getLong("total")), userId, year);
    }

    private long currentTotal(UUID userId) {
        Long value = jdbc.queryForObject("SELECT COALESCE(SUM(price), 0) FROM fixed_expenses WHERE user_id = ?", Long.class, userId);
        return value == null ? 0 : value;
    }

    public record MonthlyTotal(int year, int month, long total) {}
}
