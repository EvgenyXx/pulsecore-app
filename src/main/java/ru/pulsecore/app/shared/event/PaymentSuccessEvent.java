package ru.pulsecore.app.shared.event;

import java.util.UUID;

public record PaymentSuccessEvent(UUID playerId, int days,
                                  String amount,
                                  String currency) {
}
