package ru.pulsecore.app.notification.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.domain.PushSubscription;
import ru.pulsecore.app.notification.infrastructure.config.VapidConfig;


@Service
@RequiredArgsConstructor
@Slf4j
public class PushClient {

    private final VapidConfig vapidConfig;
    private final ObjectMapper objectMapper;


    public void sendPush(PushSubscription sub, String title, String body, String url) throws Exception {

        String payload = objectMapper.writeValueAsString(new PushPayload(title, body, url));

        PushService pushService = new PushService()
                .setPublicKey(vapidConfig.getPublicKey())
                .setPrivateKey(vapidConfig.getPrivateKey())
                .setSubject("mailto:noreply@pulsecore-app.ru");

        pushService.send(new Notification(sub.getEndpoint(), sub.getP256dh(), sub.getAuth(), payload));
    }

    private record PushPayload(String title, String body, String url) {}
}
