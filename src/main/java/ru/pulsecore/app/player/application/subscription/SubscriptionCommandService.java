package ru.pulsecore.app.player.application.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.application.player.PlayerSearchService;
import ru.pulsecore.app.shared.config.CacheNames;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Subscription;
import ru.pulsecore.app.player.infrastructure.repository.SubscriptionRepository;
import ru.pulsecore.app.shared.dto.response.MessageResponse;
import ru.pulsecore.app.shared.exception.BaseException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlayerSearchService playerSearchService;
    private final PaymentService paymentService;

    @Transactional
    public MessageResponse activateSubscriptionForPeriod(UUID playerId, LocalDateTime start, LocalDateTime end) {
        log.debug("activateSubscriptionForPeriod: playerId={}, start={}, end={}", playerId, start, end);
        if (start.isAfter(end)) {
            throw new BaseException(HttpStatus.BAD_REQUEST, "Дата начала позже даты окончания");
        }
        Player player = playerSearchService.getById(playerId);
        Subscription subscription = player.getSubscription();
        subscription.setStartedAt(start);
        subscription.setExpiresAt(end);
        subscriptionRepository.save(subscription);

        return new MessageResponse(
                String.format("Подписка выдана с %s  по %s", start, end)
        );
    }

    @CacheEvict(value = CacheNames.SUBSCRIPTION, key = CacheNames.KEY_PLAYER_ID)
    @Transactional
    public void deactivate(UUID id) {
        Player player = playerSearchService.getById(id);
        Subscription subscription = player.getSubscription();
        if (subscription != null) {
            subscription.setActive(false);
            subscriptionRepository.save(subscription);
            log.info("❌ Подписка отключена для {}", player.getEmail());
        }
    }

    @Transactional
    public void enable(UUID id) {
        Player player = playerSearchService.getById(id);
        Subscription subscription = player.getSubscription();
        if (subscription != null) {
            subscription.setActive(true);
            subscriptionRepository.save(subscription);
            log.info("Подписка включена для {}", player.getEmail());
        }
    }

    @Transactional
    @CacheEvict(value = CacheNames.SUBSCRIPTION, key = CacheNames.KEY_PLAYER_ID)
    public void adminActivate(UUID id, int days) {
        Player player = playerSearchService.getById(id);

        Subscription subscription = player.getSubscription();
        if (subscription == null) {
            subscription = Subscription.builder()
                    .player(player)
                    .build();
        }
        subscription.activate(days);
        subscriptionRepository.save(subscription);

    }

    @Transactional
    @CacheEvict(value = CacheNames.SUBSCRIPTION, key = CacheNames.KEY_PLAYER_ID)
    public void activate(UUID id, int month, BigDecimal amount,
                         String paymentId, LocalDateTime paidAt) {

        if (paymentService.existsByPaymentId(paymentId)) {
            log.warn("Платёж уже обработан: {}", paymentId);
            return;
        }

        Player player = playerSearchService.getById(id);

        Subscription subscription = player.getSubscription();
        if (subscription == null) {
            subscription = Subscription.builder()
                    .player(player)
                    .build();
        }

        subscription.activate(month * 30);
        subscriptionRepository.save(subscription);

        paymentService.save(id, amount, month,
                subscription.getId(), paymentId, paidAt);

        log.info("✅ Подписка активирована для {} на {} месяца", player.getEmail(), month);
    }


    @Transactional(propagation = Propagation.MANDATORY)
    public void save(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }


}