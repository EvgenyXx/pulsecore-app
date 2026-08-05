package ru.pulsecore.app.tournament.application.tournament;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.ResultDto;

import ru.pulsecore.app.tournament.infrastructure.exception.TournamentNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.util.PlayerNameMatcher;
import ru.pulsecore.app.tournament.infrastructure.exception.TournamentResultNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.persistence.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentResultProcessor {

    private final TournamentResultRepository tournamentResultRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentResultPersistence persistence;

    @Transactional
    public void updateResult(Long id, Double amount, Double bonus) {
        TournamentResultEntity result = tournamentResultRepository.findById(id)
                .orElseThrow(() -> new TournamentResultNotFoundException(id));
        if (amount != null) result.setAmount(amount);
        if (bonus != null) result.setBonus(bonus);
        tournamentResultRepository.save(result);
        persistence.evictCaches();
    }

    public void processResults(List<ResultDto> results,
                               UUID playerId, String playerName, TournamentEntity tournament,
                               double bonus, boolean isFinished, boolean hasRemoved, String league) {
        for (ResultDto r : results) {
            if (PlayerNameMatcher.isSamePlayer(playerName, r.getPlayer()) && isFinished) {
                persistence.save(buildEntity(playerId, tournament, r, bonus, hasRemoved, league));
            }
        }
    }

    public boolean processResults(List<ResultDto> results,
                                  UUID playerId, String playerName, Long tournamentId,
                                  double bonus, boolean isFinished, boolean hasRemoved, String league) {
        TournamentEntity tournament = tournamentRepository.findByExternalId(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        boolean found = false;
        for (ResultDto r : results) {
            if (PlayerNameMatcher.isSamePlayer(playerName, r.getPlayer())) {
                found = true;
                if (isFinished) {
                    persistence.save(buildEntity(playerId, tournament, r, bonus, hasRemoved, league));
                }
            }
        }
        return found;
    }

    private TournamentResultEntity buildEntity(UUID playerId, TournamentEntity tournament, ResultDto r,
                                               double bonus, boolean hasRemoved, String league) {
        return TournamentResultEntity.builder()
                .playerId(playerId)
                .playerName(r.getPlayer())
                .amount((double) r.getTotal())
                .date(LocalDate.parse(r.getDate()))
                .tournament(tournament)
                .isNight(bonus > 0)
                .bonus(bonus)
                .hasRemoved(hasRemoved)
                .league(league)
                .build();
    }
}