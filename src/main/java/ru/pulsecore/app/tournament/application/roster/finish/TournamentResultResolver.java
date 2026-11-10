package ru.pulsecore.app.tournament.application.roster.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.ResultDto;
import ru.pulsecore.app.tournament.application.mapping.TournamentResultMapper;
import ru.pulsecore.app.tournament.infrastructure.util.PlayerIdentity;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentResultResolver {

    private final TournamentResultMapper tournamentResultMapper;

    public Optional<TournamentResultEntity> processResults(List<ResultDto> results,
                                                           UUID playerId, String playerName,
                                                           TournamentEntity tournament,
                                                           double bonus, boolean isFinished,
                                                           boolean hasRemoved, String league) {
        if (!isFinished) {
            log.debug("Результаты: турнир не завершён, пропуск");
            return Optional.empty();
        }

        for (ResultDto r : results) {
            if (PlayerIdentity.isSamePlayer(playerName, r.getPlayer())) {
                TournamentResultEntity entity = tournamentResultMapper.buildEntity(
                        playerId, tournament, r, bonus, hasRemoved, league
                );
                return Optional.of(entity);
            }
        }

        return Optional.empty();
    }


    public List<TournamentResultEntity> processResultsRoster(List<ResultDto> results,
                                                             Map<UUID, String> roster,
                                                             TournamentEntity tournament,
                                                             double bonus,
                                                             boolean hasRemoved,
                                                             String league) {
        List<TournamentResultEntity> entities = new ArrayList<>();
        for (ResultDto r : results) {
            roster.forEach((playerId, playerName) -> {
                if (PlayerIdentity.isSamePlayer(playerName, r.getPlayer())) {
                    entities.add(tournamentResultMapper.buildEntity(playerId, tournament, r, bonus, hasRemoved, league));
                }
            });
        }
        log.debug("Результаты: найдено {} записей для сохранения", entities.size());
        return entities;
    }


}