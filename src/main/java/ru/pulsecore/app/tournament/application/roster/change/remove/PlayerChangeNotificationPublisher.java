package ru.pulsecore.app.tournament.application.roster.change.remove;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.PlayerReplacedContext;
import ru.pulsecore.app.notification.application.mail.context.PlayerTransferredContext;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import ru.pulsecore.app.tournament.application.roster.change.TransferInfo;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerChangeNotificationPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void sendReplacementNotification(PlayerData player, TournamentDto tournament) {
        log.debug("Публикация события замены: player={}, tournament={}",
                player.playerName(), tournament.getLink());

        eventPublisher.publishEvent(
                new MailNotificationEvent(
                        MailTypes.PLAYER_REPLACED,
                        new PlayerReplacedContext(
                                player.email(),
                                StringUtils.extractFirstName(player.playerName()),
                                tournament.getTitle(),
                                DateTimeUtils.formatDate(tournament.getDate().getDate()),
                                DateTimeUtils.formatTime(tournament.getDate().getDate()),
                                tournament.getHall() != null ? tournament.getHall() : "—",
                                tournament.getLeague() != null ? tournament.getLeague() : "—"
                        )
                )
        );

        log.info("Событие замены опубликовано: player={}", player.playerName());
    }

    public void sendTransferNotification(PlayerData player, TransferInfo transferInfo) {
        log.debug("Публикация события переноса: player={}, from={}, to={}",
                player.playerName(),
                transferInfo.from().getLink(),
                transferInfo.to().getLink());

        eventPublisher.publishEvent(
                new MailNotificationEvent(
                        MailTypes.PLAYER_TRANSFERRED,
                        new PlayerTransferredContext(
                                player.email(),
                                StringUtils.extractFirstName(player.playerName()),
                                transferInfo
                        )
                )
        );

        log.info("Событие переноса опубликовано: player={}", player.playerName());
    }
}