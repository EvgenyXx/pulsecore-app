package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.NewTournamentContext;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import ru.pulsecore.app.shared.event.PushNotificationEvent;
import ru.pulsecore.app.shared.util.PushMessageBuilder;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class NewTournamentEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(Map<PlayerData, List<TournamentDto>>playerDataListMap) {
        playerDataListMap.forEach((player, tournament) -> {
            if (player.notificationsEnabled()){
                tournament.forEach(tournamentDto -> sendEmail(player, tournamentDto));
            }
            if (player.pushEnabled()){
                tournament.forEach(tournamentDto -> sendPush(player, tournamentDto));
            }
        });
    }


    private void sendEmail(PlayerData player, TournamentDto tournament) {
        if (player.notificationsEnabled()) {

            String firstName = StringUtils.extractFirstName(player.playerName());
            String rawDate = tournament.getDate() != null ? tournament.getDate().getDate() : null;

            publisher.publishEvent(
                    new MailNotificationEvent(
                            MailTypes.NEW_TOURNAMENT,
                            new NewTournamentContext(
                                    player.email(),
                                    firstName,
                                    DateTimeUtils.formatDate(rawDate),
                                    DateTimeUtils.formatTime(rawDate),
                                    tournament.getHall() != null ? tournament.getHall() : "—",
                                    tournament.getLeague() != null ? tournament.getLeague() : "—",
                                    tournament.getPlayers() != null && !tournament.getPlayers().isEmpty()
                                            ? String.join("\n", tournament.getPlayers()) : "—",
                                    tournament.getLink() != null ? tournament.getLink() : "")));
        }
    }

    private void sendPush(PlayerData player,TournamentDto tournament) {
        if (player.pushEnabled()) {
            publisher.publishEvent(
                    new PushNotificationEvent(
                            player.playerId(),
                            "📋 Вы в составе!",
                            PushMessageBuilder.buildNewTournamentBody(player.playerName(), tournament),
                            "/dashboard"
                    )
            );
        }
    }

}


