package ru.pulsecore.app.tournament.application.match;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentMatchRepository;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentMatchService {

    private final TournamentMatchRepository matchRepository;
    private final TournamentMatchFactory matchFactory;

    @Transactional
    public void saveAllMatches(List<TournamentMatchEntity> matches, List<Long> tournamentIds) {
        if (matches == null || matches.isEmpty()) return;

        Set<Long> existingIds = matchRepository.findExistingTournamentIds(tournamentIds);


        List<TournamentMatchEntity> toSave = matches.stream()
                .filter(m -> !existingIds.contains(m.getTournament().getId()))
                .toList();

        if (!toSave.isEmpty()) {
            matchRepository.saveAll(toSave);
        }
    }

    @Transactional
    public void createMatches(ParsedResult parsed, TournamentEntity tournament) {
        if (matchRepository.existsByTournamentId(tournament.getId())) {
            return;
        }
        LocalDateTime playedAt = DateTimeUtils.parseDate(parsed.date()).atStartOfDay();

        List<TournamentMatchEntity> matches = parsed.matches().stream()
                .filter(Match::isCompleted)
                .map(m -> matchFactory.create(tournament, m, playedAt))
                .toList();

        if (!matches.isEmpty()) {
            matchRepository.saveAll(matches);
        }
    }


}



