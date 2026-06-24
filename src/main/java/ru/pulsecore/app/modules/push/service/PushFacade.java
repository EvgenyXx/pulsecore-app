package ru.pulsecore.app.modules.push.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.push.api.dto.PushSubscriptionRequest;
import ru.pulsecore.app.modules.push.config.VapidConfig;
import ru.pulsecore.app.modules.push.model.PushSubscription;
import ru.pulsecore.app.modules.push.repository.PushSubscriptionRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PushFacade {

    private final PushSubscriptionRepository repository;
    private final VapidConfig vapidConfig;
    private final PlayerService  playerService;

    @Transactional(readOnly = true)
    public boolean isSubscribed(UUID playerId) {
        return !repository.findByPlayerId(playerId).isEmpty();
    }

    @Transactional(readOnly = true)
    public String getVapidPublicKey() {
        return vapidConfig.getPublicKey();
    }

    public void subscribe(UUID playerId, PushSubscriptionRequest request) {
        if (repository.findByPlayerIdAndEndpoint(playerId, request.endpoint()).isPresent()) {
            log.debug("Push-подписка уже существует для playerId={}", playerId);
            return;
        }
        repository.save(PushSubscription.builder()
                .playerId(playerId)
                .endpoint(request.endpoint())
                .p256dh(request.p256dh())
                .auth(request.auth())
                .build());
        log.info("Push-подписка сохранена для playerId={}", playerId);
    }

    public void unsubscribe(UUID playerId, String endpoint) {
        repository.findByPlayerIdAndEndpoint(playerId, endpoint)
                .ifPresentOrElse(
                        sub -> {
                            repository.delete(sub);
                            log.info("Push-подписка удалена для playerId={}", playerId);
                        },
                        () -> log.debug("Push-подписка не найдена для playerId={}", playerId)
                );
    }

    public boolean togglePushEnabled(UUID playerId) {
        Player player = playerService.getById(playerId);
        player.setPushEnabled(!player.isPushEnabled());
        playerService.save(player);
        log.info("📲 Push-уведомления {} для игрока {} ({})", player.isPushEnabled() ? "включены" : "отключены", player.getName(), playerId);
        return player.isPushEnabled();
    }

    public boolean isPushEnabled(UUID playerId) {
        return playerService.getById(playerId).isPushEnabled();
    }
}