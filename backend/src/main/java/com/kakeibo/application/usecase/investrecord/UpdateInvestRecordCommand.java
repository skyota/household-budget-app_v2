package com.kakeibo.application.usecase.investrecord;

import java.time.LocalDate;
import java.util.UUID;

import com.kakeibo.domain.model.invest.InvestType;

public record UpdateInvestRecordCommand(
    Long investRecordId,
    long amount,
    LocalDate investDate,
    InvestType investType,
    String memo,
    UUID userId
) {}
