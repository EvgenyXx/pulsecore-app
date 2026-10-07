package ru.pulsecore.app.admin.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.admin.client.PlayerClient;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.BroadcastContext;
import ru.pulsecore.app.shared.dispetcher.MailDispatcher;
import ru.pulsecore.app.shared.event.MailContent;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Сервис массовых рассылок.
 * Отправляет email всем игрокам.
 * Используется в админке (BroadcastController).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BroadcastService {

    private final PlayerClient playerClient;
    private final MailDispatcher mailDispatcher;

    public BroadcastResult broadcast(String message) {
        List<PlayerData> players = playerClient.getPlayers();

        int emailSent = 0;

        Map<UUID, MailContent>content = new HashMap<>();
        for (PlayerData player : players) {
            content.put(player.id(),new MailContent(
                    MailTypes.BROADCAST,
                    new BroadcastContext(player.email(),message)
            ));

        }

        mailDispatcher.send(content);
        log.info("Рассылка завершена. Email: {}", emailSent);

        return new BroadcastResult(players.size(), emailSent);
    }


}