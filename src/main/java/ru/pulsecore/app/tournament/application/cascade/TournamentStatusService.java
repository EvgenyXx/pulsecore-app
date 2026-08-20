package ru.pulsecore.app.tournament.application.cascade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentStatusService {

    private final TournamentRepository tournamentRepository;

    public void markAsCancelled(TournamentEntity tournament, ParsedResult parsed, String url) {
        tournament.setTime(parsed.time());
        tournament.setExternalId(parsed.tournamentId());
        tournament.setLink(url);
        tournament.setDate(parsed.date() != null ? LocalDate.parse(parsed.date()) : null);
        tournament.setCancelled(true);
        tournament.setFinished(true);
        tournament.setProcessed(true);
        tournament.setStarted(true);
        tournamentRepository.save(tournament);
    }
}