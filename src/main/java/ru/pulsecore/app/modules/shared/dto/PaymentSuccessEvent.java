package ru.pulsecore.app.modules.shared.dto;

import java.util.UUID;

public record PaymentSuccessEvent(UUID playerId, int days) {}
