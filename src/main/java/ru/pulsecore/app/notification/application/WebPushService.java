package ru.pulsecore.app.notification.application;


import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.infrastructure.client.PushClient;
import ru.pulsecore.app.notification.domain.PushSubscription;
import ru.pulsecore.app.notification.infrastructure.config.NotificationAsyncConfig;
import ru.pulsecore.app.notification.infrastructure.repository.PushSubscriptionRepository;
import ru.pulsecore.app.shared.event.PushContent;
import java.security.Security;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class WebPushService {

    private final PushSubscriptionRepository subscriptionRepository;
    private final PushClient pushClient;
    private final Executor pushHttpExecutor;

    public WebPushService(PushSubscriptionRepository subscriptionRepository,
                          PushClient pushClient,
                          @Qualifier(NotificationAsyncConfig.PUSH_HTTP_EXECUTOR) Executor pushHttpExecutor) {
        this.subscriptionRepository = subscriptionRepository;
        this.pushClient = pushClient;
        this.pushHttpExecutor = pushHttpExecutor;
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public void sendToPlayers(Map<UUID, PushContent> contentByPlayer) {
        List<PushSubscription> subscriptions = subscriptionRepository.findByPlayerIdIn(contentByPlayer.keySet());

        if (subscriptions.isEmpty()) {
            log.debug("Нет push-подписок для playerIds={}", contentByPlayer.keySet());
            return;
        }

        subscriptions.forEach(sub -> {
                    PushContent pushContent = contentByPlayer.get(sub.getPlayerId());
                    if (pushContent == null) return;
                    CompletableFuture.runAsync(() ->
                            sendOne(sub, pushContent.title(), pushContent.body(), pushContent.url()), pushHttpExecutor);
                }

        );
    }

    private void sendOne(PushSubscription sub, String title, String body, String url) {
        try {
            pushClient.sendPush(sub, title, body, url);
        } catch (Exception e) {
            log.error("Push fail: endpoint={}, error={}", sub.getEndpoint(), e.getMessage());
            if (isGone(e)) {
                subscriptionRepository.delete(sub);
            }
        }
    }

    private boolean isGone(Exception e) {
        return e.getMessage() != null && e.getMessage().contains("410");
    }

}