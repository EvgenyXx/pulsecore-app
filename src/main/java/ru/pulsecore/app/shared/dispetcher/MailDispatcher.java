package ru.pulsecore.app.shared.dispetcher;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.event.MailBatchEvent;
import ru.pulsecore.app.shared.event.MailContent;
import ru.pulsecore.app.shared.event.SingleMailEvent;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailDispatcher {

    private final ApplicationEventPublisher publisher;

    /**
     * Админское — без email (стратегия сама знает, куда)
     */
    public void send(MailContent content) {
        if (content == null) return;
        publisher.publishEvent(new SingleMailEvent(null, content));
    }

    /**
     * Батч — Map<UUID, MailContent>
     */
    public void send(Map<UUID, MailContent> contentByPlayer) {
        if (contentByPlayer == null || contentByPlayer.isEmpty()) return;
        publisher.publishEvent(new MailBatchEvent(contentByPlayer));
    }

    /**
     * Одиночное — email + content (без playerId)
     */
    public void send(String email, MailContent content) {
        if (email == null || content == null) return;
        publisher.publishEvent(new SingleMailEvent(email, content));
    }
}