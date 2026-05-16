package ru.pulsecore.app.modules.player.service.subscribion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.player.api.dto.SubscriptionStatusResponse;
import ru.pulsecore.app.modules.player.domain.Subscription;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionFacade {

    private final SubscriptionService subscriptionService;

    public SubscriptionStatusResponse getSubscription(UUID playerId) {
        Subscription sub = subscriptionService.getByPlayerId(playerId);
        if (sub == null) {
            return new SubscriptionStatusResponse(false, null, null);
        }
        return SubscriptionStatusResponse.builder()
                .active(sub.isActiveNow())
                .expiresAt(sub.getExpiresAt() != null ? sub.getExpiresAt().toString() : null)
                .startedAt(sub.getStartedAt() != null ? sub.getStartedAt().toString() : null)
                .build();
    }
}