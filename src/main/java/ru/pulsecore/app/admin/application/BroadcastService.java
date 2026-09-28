package ru.pulsecore.app.admin.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.admin.client.PlayerClient;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.BroadcastContext;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import ru.pulsecore.app.shared.dto.response.PlayerData;

import java.util.List;

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
    private final ApplicationEventPublisher eventPublisher;

    public BroadcastResult broadcast(String message) {
        List<PlayerData> players = playerClient.getPlayers();

        int emailSent = 0;

        for (PlayerData player : players) {
            sendEmail(player.email(), message);
            emailSent++;
        }

        log.info("Рассылка завершена. Email: {}", emailSent);

        return new BroadcastResult(players.size(), emailSent);
    }

    private void sendEmail(String email, String message) {
        eventPublisher.publishEvent(
                new MailNotificationEvent(
                        MailTypes.BROADCAST,
                        new BroadcastContext(email, message)
                )
        );
    }
}