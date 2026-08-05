package ru.pulsecore.app.shared.dto;

import java.util.UUID;

public record PaymentSuccessEvent(UUID playerId, int days) {}
