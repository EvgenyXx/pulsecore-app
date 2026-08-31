package ru.pulsecore.app.tournament.application.compare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.api.dto.response.*;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentMatchRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class CompareService {

    private final TournamentResultRepository repository;
    private final TournamentMatchRepository matchRepository;
    public PlayerH2HResponseDto getH2H(String player1Name, String player2Name, LocalDate start, LocalDate end) {
        LocalDateTime effectiveStart = start != null
                ? start.atStartOfDay()
                : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime effectiveEnd = end != null
                ? end.plusDays(1).atStartOfDay()
                : LocalDate.now().plusDays(1).atStartOfDay();

        log.debug("H2H запрос: player1={}, player2={}, start={}, end={}, effectiveStart={}, effectiveEnd={}",
                player1Name, player2Name, start, end, effectiveStart, effectiveEnd);

        PlayerH2HSummaryDto summary = PlayerH2HSummaryDto.from(
                matchRepository.findH2HSummary(player1Name, player2Name, effectiveStart, effectiveEnd)
        );

        log.debug("H2H summary: total={}, player1Wins={}, player2Wins={}",
                summary.getTotalMatches(), summary.getPlayer1Wins(), summary.getPlayer2Wins());

        List<PlayerH2HStageDto> stages = matchRepository.findH2HByStage(player1Name, player2Name, effectiveStart, effectiveEnd)
                .stream()
                .map(PlayerH2HStageDto::from)
                .toList();

        log.debug("H2H stages: {}", stages);

        return PlayerH2HResponseDto.builder()
                .player1Name(player1Name)
                .player2Name(player2Name)
                .summary(summary)
                .stages(stages)
                .build();
    }


    public List<PlayerCompareDto> getPlayersForCompare(LocalDate start, LocalDate end) {

        LocalDate effectiveStart = start != null ? start : LocalDate.of(2000, 1, 1);
        LocalDate effectiveEnd = end != null ? end : LocalDate.now();

        return repository.findPlayersForCompare(effectiveStart, effectiveEnd)
                .stream()
                .map(PlayerCompareDto::from)
                .toList();
    }

    public List<PlayerMatchStatsDto> getPlayersMatchStats(LocalDate start, LocalDate end) {
        LocalDateTime effectiveStart = start != null
                ? start.atStartOfDay()
                : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime effectiveEnd = end != null
                ? end.plusDays(1).atStartOfDay()
                : LocalDate.now().plusDays(1).atStartOfDay();

        return matchRepository.findPlayersMatchStats(effectiveStart, effectiveEnd)
                .stream()
                .map(p -> PlayerMatchStatsDto.builder()
                        .playerName(p.getPlayerName())
                        .groupWinPercent(p.getGroupWinPercent())
                        .semifinalWinPercent(p.getSemifinalWinPercent())
                        .thirdPlaceWinPercent(p.getThirdPlaceWinPercent())
                        .finalWinPercent(p.getFinalWinPercent())
                        .build())
                .toList();
    }



}