package ru.pulsecore.app.player.application.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.shared.config.CacheNames;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Subscription;
import ru.pulsecore.app.player.infrastructure.persistence.repository.SubscriptionRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlayerSearchService playerSearchService;

    @CacheEvict(value = CacheNames.SUBSCRIPTION, key = CacheNames.KEY_PLAYER_ID)
    @Transactional
    public void deactivate(UUID playerId) {
        Player player = playerSearchService.getById(playerId);
        Subscription subscription = player.getSubscription();
        if (subscription != null) {
            subscription.setActive(false);
            subscriptionRepository.save(subscription);
            log.info("❌ Подписка отключена для {}", player.getEmail());
        }
    }

    @Transactional
    @CacheEvict(value = CacheNames.SUBSCRIPTION, key = CacheNames.KEY_PLAYER_ID)
    public void activate(UUID playerId, int days) {
        Player player = playerSearchService.getById(playerId);

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

    @Transactional(propagation = Propagation.MANDATORY)
    public void save(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }



}