package com.kakeibo.presentation.dto;

import java.time.LocalDate;

import com.kakeibo.domain.model.invest.InvestType;

public record InvestRecordResponse(
    Long id,
    long amount,
    LocalDate investDate,
    InvestType investType,
    String memo
) {}
