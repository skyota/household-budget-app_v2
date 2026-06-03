package com.kakeibo.application.usecase.investrecord;

import java.util.UUID;

public record DeleteInvestRecordCommand(
    Long investRecordId,
    UUID userId
) {}
