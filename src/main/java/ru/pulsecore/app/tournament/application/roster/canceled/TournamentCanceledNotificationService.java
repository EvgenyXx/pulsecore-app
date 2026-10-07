package ru.pulsecore.app.tournament.application.roster.canceled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.CanceledTournamentContext;
import ru.pulsecore.app.shared.dispetcher.MailDispatcher;
import ru.pulsecore.app.shared.dispetcher.PushDispatcher;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.MailContent;
import ru.pulsecore.app.shared.event.PushContent;
import ru.pulsecore.app.shared.util.PushMessageBuilder;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentCanceledNotificationService {

    private final PlayerClient playerClient;
    private final PushDispatcher pushDispatcher;
    private final MailDispatcher mailDispatcher;

    public void sendCancelled(List<PlayerNotification> notifications, TournamentEntity tournamentEntity) {
        if (notifications == null || notifications.isEmpty()) return;

        log.debug("Отмена: начало отправки уведомлений для {} игроков", notifications.size());

        Set<UUID> playerIds = notifications.stream()
                .map(PlayerNotification::getPlayerId)
                .collect(Collectors.toSet());

        Map<UUID, PlayerData> playerMap = playerClient.getPlayerDataByIds(playerIds).stream()
                .collect(Collectors.toMap(PlayerData::id, p -> p));

        pushDispatcher.send(buildPushContent(notifications, playerMap, tournamentEntity));
        mailDispatcher.send(buildMailContent(notifications, playerMap, tournamentEntity));

        log.info("Отмена турнира: уведомления отправлены {} игрокам", notifications.size());
    }

    // ==================== PUSH ====================

    private Map<UUID, PushContent> buildPushContent(
            List<PlayerNotification> notifications,
            Map<UUID, PlayerData> playerMap,
            TournamentEntity tournament) {

        String time = tournament.getTime() != null ? tournament.getTime() : "?";
        String date = tournament.getDate() != null ? tournament.getDate().toString() : "?";
        String link = tournament.getLink();
        String body = PushMessageBuilder.buildCancelledBody(date, time);

        Map<UUID, PushContent> result = new HashMap<>();

        for (PlayerNotification pn : notifications) {
            PlayerData player = playerMap.get(pn.getPlayerId());
            if (player == null) continue;

            result.put(player.id(), new PushContent(
                    "Турнир отменён",
                    body,
                    link
            ));
        }

        return result;
    }

    // ==================== MAIL ====================

    private Map<UUID, MailContent> buildMailContent(
            List<PlayerNotification> notifications,
            Map<UUID, PlayerData> playerMap,
            TournamentEntity tournament) {

        String time = tournament.getTime() != null ? tournament.getTime() : "?";
        String date = tournament.getDate() != null ? tournament.getDate().toString() : "?";
        String link = tournament.getLink();

        Map<UUID, MailContent> result = new HashMap<>();

        for (PlayerNotification pn : notifications) {
            PlayerData player = playerMap.get(pn.getPlayerId());
            if (player == null) continue;

            result.put(player.id(), new MailContent(
                    MailTypes.CANCELED_TOURNAMENT,
                    new CanceledTournamentContext(
                            player.email(),
                            time,
                            date,
                            link
                    )
            ));
        }

        return result;
    }
}