package com.kakeibo.application.usecase.incomerecord;

import java.util.UUID;

public record DeleteIncomeRecordCommand(
    Long incomeRecordId,
    UUID userId
) {}
