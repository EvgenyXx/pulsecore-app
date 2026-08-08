package ru.pulsecore.app.tournament.application.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.NewTournamentContext;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import ru.pulsecore.app.shared.event.PushNotificationEvent;
import ru.pulsecore.app.shared.util.PushMessageBuilder;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentDiscoveryService {

    private final PlayerClient playerClient;
    private final UpcomingTournamentService upcomingTournamentService;
    private final TournamentFilter filter;
    private final TournamentSaver saver;
    private final ApplicationEventPublisher publisher;


    @Transactional
    public void checkNewTournaments() {
        List<PlayerData> activePlayers = playerClient.getAll();

        if (activePlayers.isEmpty()) return;

        List<String> playerName = activePlayers.stream().map(PlayerData::playerName).toList();

        Map<String, List<TournamentDto>> allFound = upcomingTournamentService.findPlayerTournaments(playerName);


        Map<PlayerData, List<TournamentDto>> notification = new HashMap<>();
        for (PlayerData player : activePlayers) {
            List<TournamentDto> playerTournaments = allFound.getOrDefault(player.playerName(), List.of());
            List<TournamentDto> newTournaments = filter.findNew(player.playerId(), playerTournaments);

            if (!newTournaments.isEmpty()) {
                notification.put(player, newTournaments);
            }
        }
        saver.saveAll(notification);

        sendEmailNotifications(notification);
        sendPushNotifications(notification);
        log.info("Новые турниры: игроков={}, всего турниров={}", notification.size(),
                notification.values().stream().mapToInt(List::size).sum());
    }

    private void sendEmailNotifications(
            Map<PlayerData, List<TournamentDto>> playerTournaments
    ) {
        for (Map.Entry<PlayerData, List<TournamentDto>> entry : playerTournaments.entrySet()) {

            PlayerData player = entry.getKey();
            List<TournamentDto> tournaments = entry.getValue();
            if (player.notificationsEnabled()) {
                tournaments.forEach(t -> {
                    String firstName = StringUtils.extractFirstName(player.playerName());
                    String rawDate = t.getDate() != null ? t.getDate().getDate() : null;
                    //todo потом добавить отправку батчем ивент
                    publisher.publishEvent(
                            new MailNotificationEvent(
                                    MailTypes.NEW_TOURNAMENT,
                                    new NewTournamentContext(
                                            player.email(),
                                            firstName,
                                            DateTimeUtils.formatDate(rawDate),
                                            DateTimeUtils.formatTime(rawDate),
                                            t.getHall() != null ? t.getHall() : "—",
                                            t.getLeague() != null ? t.getLeague() : "—",
                                            t.getPlayers() != null && !t.getPlayers().isEmpty()
                                                    ? String.join("\n", t.getPlayers()) : "—",
                                            t.getLink() != null ? t.getLink() : "")));
                });

            }
        }


    }

    private void sendPushNotifications(Map<PlayerData, List<TournamentDto>> playerTournaments) {
        for (Map.Entry<PlayerData, List<TournamentDto>> entry : playerTournaments.entrySet()) {

            PlayerData player = entry.getKey();
            List<TournamentDto> tournaments = entry.getValue();
            if (player.pushEnabled()) {

                tournaments.forEach(t -> publisher.publishEvent(
                        new PushNotificationEvent(
                                player.playerId(),
                                "📋 Вы в составе!",
                                PushMessageBuilder.buildNewTournamentBody(player.playerName(), t),
                                "/dashboard"
                        )
                ));
            }

        }

    }

}