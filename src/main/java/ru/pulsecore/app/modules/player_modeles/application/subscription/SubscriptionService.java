package ru.pulsecore.app.modules.player_modeles.application.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.config.CacheNames;
import ru.pulsecore.app.modules.player_modeles.entity.Player;
import ru.pulsecore.app.modules.player_modeles.entity.Subscription;
import ru.pulsecore.app.modules.player_modeles.infrastructure.persistence.repository.SubscriptionRepository;
import ru.pulsecore.app.modules.player_modeles.application.player.PlayerService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlayerService playerService;

    @CacheEvict(value = CacheNames.SUBSCRIPTION, key = CacheNames.KEY_PLAYER_ID)
    public void deactivate(UUID playerId) {
        Player player = playerService.getById(playerId);
        Subscription subscription = player.getSubscription();
        if (subscription != null) {
            subscription.setActive(false);
            subscriptionRepository.save(subscription);
            log.info("❌ Подписка отключена для {}", player.getEmail());
        }
    }

    @CacheEvict(value = CacheNames.SUBSCRIPTION, key = CacheNames.KEY_PLAYER_ID)
    public void activate(UUID playerId, int days) {
        Player player = playerService.getById(playerId);

        Subscription subscription = player.getSubscription();
        if (subscription == null) {
            subscription = Subscription.builder()
                    .player(player)
                    .build();
        }
        subscription.activate(days);
        subscriptionRepository.save(subscription);

        log.info("✅ Подписка активирована для {} на {} дней", player.getEmail(), days);
    }

    public Subscription getByPlayerId(UUID playerId) {
        Player player = playerService.getById(playerId);
        return player.getSubscription();
    }

    @Cacheable(value = CacheNames.SUBSCRIPTION, key = CacheNames.KEY_PLAYER_ID)
    public boolean hasActiveSubscription(UUID playerId) {
        var sub = subscriptionRepository.findByPlayerId(playerId);
        boolean active = sub.map(Subscription::isActiveNow).orElse(false);
        if (active) {
            Player player = sub.get().getPlayer();
            log.debug("✅ Активная подписка: {}", player.getName());
        }
        return active;
    }
}