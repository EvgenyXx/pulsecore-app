package ru.pulsecore.app.notification.application;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.infrastructure.client.PushClient;
import ru.pulsecore.app.notification.domain.PushSubscription;
import ru.pulsecore.app.notification.infrastructure.repository.PushSubscriptionRepository;
import java.security.Security;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebPushService {

    private final PushSubscriptionRepository subscriptionRepository;
    private final PushClient pushClient;


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public void sendToPlayer(UUID playerId, String title, String body, String url) {
        List<PushSubscription> subscriptions = subscriptionRepository.findByPlayerId(playerId);
        if (subscriptions.isEmpty()) {
            log.debug("Нет push-подписок для id={}", playerId);
            return;
        }
        for (PushSubscription sub : subscriptions) {
            try {
                pushClient.sendPush(sub, title, body, url);
                log.debug("Push отправлен id={}", playerId);
            } catch (Exception e) {
                log.error("Ошибка отправки пуша для id={}: {}", playerId, e.getMessage());
                if (e.getMessage() != null && e.getMessage().contains("410")) {
                    subscriptionRepository.delete(sub);
                    log.info("Удалена невалидная подписка для id={}", playerId);
                }
            }
        }
    }

}