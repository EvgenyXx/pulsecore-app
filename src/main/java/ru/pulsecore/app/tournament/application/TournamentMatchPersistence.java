package ru.pulsecore.app.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.api.dto.response.TournamentMatchDto;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentMatchRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.TournamentMatchProjection;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentMatchPersistence {

    private final TournamentMatchRepository matchRepository;
    private final TournamentResultRepository resultRepository;

    @Transactional(readOnly = true)
    public List<TournamentMatchDto> getMatchesByResultId(Long resultId) {
        log.debug("Матчи: запрос по resultId={}", resultId);

        TournamentResultEntity result = resultRepository.findById(resultId)
                .orElseThrow(() -> {
                    log.warn("Матчи: результат не найден resultId={}", resultId);
                    return new IllegalArgumentException("Результат не найден: " + resultId);
                });

        Long tournamentId = result.getTournament().getId();
        log.debug("Матчи: tournamentId={}", tournamentId);

        return matchRepository.findMatchesByTournamentId(tournamentId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private TournamentMatchDto toDto(TournamentMatchProjection p) {
        return TournamentMatchDto.builder()
                .stage(p.getStage())
                .player1Name(StringUtils.shortName(p.getPlayer1Name()))
                .player2Name(StringUtils.shortName(p.getPlayer2Name()))
                .score(p.getScore())
                .winnerName(StringUtils.shortName(p.getWinnerName()))
                .playedAt(p.getPlayedAt())
                .build();
    }
}