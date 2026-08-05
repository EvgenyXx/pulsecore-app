package ru.pulsecore.app.tournament.application.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.persistence.mapper.TournamentStatusMapper;
import ru.pulsecore.app.tournament.infrastructure.persistence.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TournamentSyncService {

    private final TournamentRepository tournamentRepository;
    private final TournamentStatusMapper statusMapper;

    public TournamentEntity sync(ParsedResult parsed, String link) {
        TournamentEntity t = tournamentRepository
                .findByExternalId(parsed.tournamentId())
                .orElseGet(TournamentEntity::new);

        t.setExternalId(parsed.tournamentId());
        t.setLink(link);


       statusMapper.apply(t,parsed.status());


        if (!parsed.results().isEmpty()) {
            t.setDate(LocalDate.parse(parsed.results().get(0).getDate()));
        }

        return tournamentRepository.save(t);
    }
}