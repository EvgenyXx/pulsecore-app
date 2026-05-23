package ru.pulsecore.app.modules.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebPushService {

    private final PushSubscriptionRepository subscriptionRepository;
    private final VapidConfig vapidConfig;
    private final ObjectMapper objectMapper;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public void sendToPlayer(UUID playerId, String title, String body, String url) {
        List<PushSubscription> subscriptions = subscriptionRepository.findByPlayerId(playerId);
        if (subscriptions.isEmpty()) {
            log.debug("Нет push-подписок для playerId={}", playerId);
            return;
        }
        for (PushSubscription sub : subscriptions) {
            try {
                sendPush(sub, title, body, url);
                log.info("Push отправлен playerId={}", playerId);
            } catch (Exception e) {
                log.error("Ошибка отправки пуша для playerId={}: {}", playerId, e.getMessage());
                if (e.getMessage() != null && e.getMessage().contains("410")) {
                    subscriptionRepository.delete(sub);
                    log.info("Удалена невалидная подписка для playerId={}", playerId);
                }
            }
        }
    }

    private void sendPush(PushSubscription sub, String title, String body, String url) throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "title", title,
                "body", body,
                "url", url,
                "tag", "tournament"
        ));

        PushService pushService = new PushService()
                .setPublicKey(vapidConfig.getPublicKey())
                .setPrivateKey(vapidConfig.getPrivateKey())
                .setSubject("mailto:noreply@pulsecore-app.ru");

        pushService.send(new Notification(sub.getEndpoint(), sub.getP256dh(), sub.getAuth(), payload));
    }
}