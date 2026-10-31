package com.kakeibo.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakeibo.presentation.dto.LivingBudgetRequest;
import com.kakeibo.presentation.dto.LivingBudgetResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/living-budget")
public class LivingBudgetController {
    private static final long DEFAULT_AMOUNT = 65_000;
    private final JdbcTemplate jdbc;

    public LivingBudgetController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public ResponseEntity<LivingBudgetResponse> get(@RequestAttribute("authenticatedUserId") UUID userId) {
        var result = jdbc.query("SELECT amount FROM living_budget_settings WHERE user_id = ?",
            (rs, row) -> rs.getLong("amount"), userId);
        return ResponseEntity.ok(new LivingBudgetResponse(result.isEmpty() ? DEFAULT_AMOUNT : result.get(0)));
    }

    @PutMapping
    public ResponseEntity<LivingBudgetResponse> save(
        @Valid @RequestBody LivingBudgetRequest request,
        @RequestAttribute("authenticatedUserId") UUID userId
    ) {
        jdbc.update("""
            INSERT INTO living_budget_settings (user_id, amount) VALUES (?, ?)
            ON CONFLICT (user_id) DO UPDATE SET amount = EXCLUDED.amount
            """, userId, request.amount());
        return ResponseEntity.ok(new LivingBudgetResponse(request.amount()));
    }
}
