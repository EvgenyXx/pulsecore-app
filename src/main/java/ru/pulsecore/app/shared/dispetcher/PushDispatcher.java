package ru.pulsecore.app.shared.dispetcher;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.event.PushContent;
import ru.pulsecore.app.shared.event.PushNotificationEvent;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PushDispatcher {

    private final ApplicationEventPublisher publisher;

    public void send(Map<UUID, PushContent> contentByPlayer) {
        if (contentByPlayer == null || contentByPlayer.isEmpty()) return;
        publisher.publishEvent(new PushNotificationEvent(contentByPlayer));
    }
}