package ru.pulsecore.app.modules.tournament.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.tournament.domain.ParsedResult;
import ru.pulsecore.app.modules.tournament.mapper.TournamentStatusMapper;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentEntity;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentRepository;

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

        // ✅ корректные флаги
       statusMapper.apply(t,parsed.status());

        // ✅ дата
        if (!parsed.results().isEmpty()) {
            t.setDate(LocalDate.parse(parsed.results().get(0).getDate()));
        }

        return tournamentRepository.save(t);
    }
}