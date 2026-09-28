package ru.pulsecore.app.player.application.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.player.application.mapper.SubscriptionMapper;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.infrastructure.repository.SubscriptionRepository;
import ru.pulsecore.app.shared.config.CacheNames;
import ru.pulsecore.app.shared.dto.response.SubscriptionStatusResponse;
import ru.pulsecore.app.player.domain.Subscription;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionQueryService {


    private final PlayerSearchService  playerSearchService;
    private final SubscriptionRepository  subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;




    public SubscriptionStatusResponse getSubscription(UUID playerId) {
        Player player = playerSearchService.getById(playerId);
        Subscription sub = player.getSubscription();
        if (sub == null) {
            return new SubscriptionStatusResponse(false, null, null);
        }
        return subscriptionMapper.toStatusResponse(sub);
    }


    @Cacheable(value = CacheNames.SUBSCRIPTION, key = CacheNames.KEY_PLAYER_ID)
    public boolean hasActiveSubscription(UUID id) {
        var sub = subscriptionRepository.findByPlayerId(id);
        boolean active = sub.map(Subscription::isActiveNow).orElse(false);
        if (active) {
            Player player = sub.get().getPlayer();
            log.debug("✅ Активная подписка: {}", player.getName());
        }
        return active;
    }

    public List<Subscription> findExpired() {
        return subscriptionRepository.findExpired();
    }

    public Set<UUID> findExpiringPlayerIds(LocalDate date) {
        return subscriptionRepository.findExpiringPlayerIds(date);
    }
}