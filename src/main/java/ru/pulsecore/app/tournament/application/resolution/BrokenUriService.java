package ru.pulsecore.app.tournament.application.resolution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.notification.application.mail.MailType;
import ru.pulsecore.app.notification.application.mail.context.admin.BrokenUriContext;
import ru.pulsecore.app.shared.dispetcher.MailDispatcher;
import ru.pulsecore.app.shared.event.MailContent;
import ru.pulsecore.app.shared.infrastructure.audit.AuditWriter;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;



@Slf4j
@Service
@RequiredArgsConstructor
public class BrokenUriService {

    private final TournamentRepository tournamentRepository;
    private final MailDispatcher mailDispatcher;
    private final AuditWriter auditWriter;

    @Transactional
    public void handle(String url) {
        tournamentRepository.findByLink(url).ifPresent(tournament -> {
            tournamentRepository.markAsBroken(tournament.getLink());

            Set<UUID> playerIds = tournament.getNotifications()
                    .stream()
                    .map(PlayerNotification::getPlayerId)
                    .collect(Collectors.toSet());

            auditWriter.write("broken",
                    "турнир=" + tournament.getExternalId() +
                            " | link=" + tournament.getLink() +
                            " | игроков=" + playerIds.size());

            mailDispatcher.send(new MailContent(
                    MailType.BROKEN_URI,
                    new BrokenUriContext(
                            tournament.getLink(),
                            tournament.getDate(),
                            tournament.getTime(),
                            tournament.getExternalId(),
                            playerIds
                    )
            ));
        });
    }
}