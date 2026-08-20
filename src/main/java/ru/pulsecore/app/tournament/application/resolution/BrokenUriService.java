package ru.pulsecore.app.tournament.application.resolution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.admin.BrokenUriContext;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.util.Set;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class BrokenUriService {

    private final TournamentRepository tournamentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(TournamentEntity tournament, Set<UUID> playerIds) {
        log.warn("Пришла битая ссылка: {}", tournament.getLink());
        tournamentRepository.markAsBroken(tournament.getLink());

        eventPublisher.publishEvent(new MailNotificationEvent(
                MailTypes.BROKEN_URI,
                new BrokenUriContext(
                        tournament.getLink(),
                        tournament.getDate(),
                        tournament.getTime(),
                        tournament.getExternalId(),
                        playerIds
                )
        ));
    }
}