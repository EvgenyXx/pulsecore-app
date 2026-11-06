package ru.pulsecore.app.shared.dispetcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.MailBatchEvent;
import ru.pulsecore.app.shared.event.MailContent;
import ru.pulsecore.app.shared.event.SingleMailEvent;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailDispatcher {

    private final ApplicationEventPublisher publisher;

    /**
     * Админское — без email (стратегия сама знает, куда)
     */
    public void send(MailContent content) {
        if (content == null) return;
        publisher.publishEvent(new SingleMailEvent(null, null, content));
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
    public void send(PlayerData playerData, String email, MailContent content) {
        log.debug("MailDispatcher.send: playerData={}, email={}, content={}", playerData, email, content);
        if (email == null || content == null) return;
        publisher.publishEvent(new SingleMailEvent(playerData, email, content));
    }
}