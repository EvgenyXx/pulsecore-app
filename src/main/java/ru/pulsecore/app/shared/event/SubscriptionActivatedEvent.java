package ru.pulsecore.app.shared.event;

import java.util.UUID;

public record SubscriptionActivatedEvent(
        UUID playerId,
        int days,
        String amount,
        String currency

)

{
}
