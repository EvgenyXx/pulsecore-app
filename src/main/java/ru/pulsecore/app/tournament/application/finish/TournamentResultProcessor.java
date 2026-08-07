package ru.pulsecore.app.tournament.application.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.pulsecore.app.shared.dto.response.ResultDto;


import ru.pulsecore.app.tournament.infrastructure.util.PlayerNameMatcher;

import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentResultProcessor {


    private final TournamentResultPersistence persistence;

    public List<TournamentResultEntity> processResults(List<ResultDto> results,
                               UUID playerId,
                               String playerName,
                               TournamentEntity tournament,
                               double bonus, boolean isFinished,
                               boolean hasRemoved, String league) {
        if (!isFinished) return List.of();
        List<TournamentResultEntity> entities = new ArrayList<>();
        for (ResultDto r : results) {
            if (PlayerNameMatcher.isSamePlayer(playerName, r.getPlayer())) {
                entities.add(buildEntity(playerId, tournament, r, bonus, hasRemoved, league));
            }
        }
        return  entities;
    }

    public void saveAll(List<TournamentResultEntity> entities) {
        if (!entities.isEmpty()) {
            persistence.saveRoster(entities);
        }
    }


    public void processResultsRoster(List<ResultDto> results,
                                     Map<UUID, String> roster,
                                     TournamentEntity tournament,
                                     double bonus,
                                     boolean isFinished,
                                     boolean hasRemoved,
                                     String league) {
        if (!isFinished) return;
        List<TournamentResultEntity> entities = new ArrayList<>();
        for (ResultDto r : results) {
            roster.forEach((playerId, playerName) -> {
                if (PlayerNameMatcher.isSamePlayer(playerName, r.getPlayer())) {
                    entities.add(buildEntity(playerId, tournament, r, bonus, hasRemoved, league));
                }
            });
        }
        if (!entities.isEmpty()) {
            persistence.saveRoster(entities);
        }
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