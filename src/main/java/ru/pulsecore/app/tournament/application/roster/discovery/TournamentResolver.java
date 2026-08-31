package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import ru.pulsecore.app.notification.infrastructure.factory.TournamentFactory;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentResolver {

    private final TournamentRepository tournamentRepository;
    private final TournamentFactory tournamentFactory;

    public TournamentEntity resolveTournament(
            TournamentDto t,
            Set<Long> seen,
            List<TournamentEntity> newTournaments,
            List<TournamentEntity> updatedTournaments) {

        return tournamentRepository
                .findByLink(t.getLink())
                .map(existing -> handleExisting(existing, t, updatedTournaments))
                .orElseGet(() -> handleNew(t, seen, newTournaments));
    }

    private TournamentEntity handleExisting(
            TournamentEntity existing,
            TournamentDto t,
            List<TournamentEntity> updatedTournaments) {

        log.debug("Сохранение: найден существующий турнир={}, link={}",
                existing.getExternalId(), t.getLink());

        if (!existing.getExternalId().equals(t.getId())) {
            log.debug("Сохранение: externalId изменился {} -> {}",
                    existing.getExternalId(), t.getId());
            existing.setExternalId(t.getId());
            updatedTournaments.add(existing);
        }
        return existing;
    }

    private TournamentEntity handleNew(
            TournamentDto t,
            Set<Long> seen,
            List<TournamentEntity> newTournaments) {

        if (seen.add(t.getId())) {
            log.debug("Сохранение: создание нового турнира={}", t.getId());
            TournamentEntity newT = tournamentFactory.create(t);
            newTournaments.add(newT);
            return newT;
        }
        log.debug("Сохранение: дубликат в текущем батче, id={}", t.getId());
        return newTournaments.stream()
                .filter(nt -> nt.getExternalId().equals(t.getId()))
                .findFirst()
                .orElseThrow();
    }
}