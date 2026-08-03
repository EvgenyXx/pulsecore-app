package ru.pulsecore.app.modules.tournament_module.service.tornament;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.core.dto.ResultDto;

import ru.pulsecore.app.modules.shared.exception.TournamentNotFoundException;
import ru.pulsecore.app.modules.shared.util.PlayerNameMatcher;
import ru.pulsecore.app.modules.tournament_module.exception.TournamentResultNotFoundException;
import ru.pulsecore.app.modules.tournament_module.entity.TournamentEntity;
import ru.pulsecore.app.modules.tournament_module.entity.TournamentResultEntity;
import ru.pulsecore.app.modules.tournament_module.repo.TournamentRepository;
import ru.pulsecore.app.modules.tournament_module.repo.TournamentResultRepository;

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