package ru.pulsecore.app.tournament.application.compare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.api.dto.response.SalaryH2HPlayerDto;
import ru.pulsecore.app.tournament.api.dto.response.SalaryH2HResponseDto;
import ru.pulsecore.app.tournament.application.compare.assembler.SalaryH2HAssembler;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentMatchRepository;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentResultRepository;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.LeagueStatProjection;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PlayerCompareResponse;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PlayerMatchStatsProjection;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaryH2HService {

    private final TournamentResultRepository resultRepository;
    private final TournamentMatchRepository matchRepository;
    private final SalaryH2HCalculator calculator;
    private final SalaryH2HAssembler assembler;

    public SalaryH2HResponseDto getSalaryH2H(String player1Name, String player2Name, LocalDate start, LocalDate end) {
        List<String> names = List.of(player1Name.toLowerCase(), player2Name.toLowerCase());

        LocalDateTime effectiveStart = start.atStartOfDay();
        LocalDateTime effectiveEnd = end.plusDays(1).atStartOfDay();

        List<PlayerCompareResponse> moneyStats = resultRepository.findPlayersForCompare(names, start, end);
        List<PlayerMatchStatsProjection> matchStats = matchRepository.findWinrateForCompare(names, effectiveStart, effectiveEnd);

        List<LeagueStatProjection> leagueStats = resultRepository.getAllLeaguesStatsByPeriod(start, end);
        Map<String, Double> leagueAverages = leagueStats.stream()
                .collect(Collectors.toMap(
                        LeagueStatProjection::getLeague,
                        LeagueStatProjection::getAvg
                ));

        SalaryH2HPlayerDto p1 = assembler.assemblePlayerDto(player1Name, moneyStats, matchStats);
        SalaryH2HPlayerDto p2 = assembler.assemblePlayerDto(player2Name, moneyStats, matchStats);

        double avg1 = leagueAverages.getOrDefault(p1.getLeague(), 1.0);
        double avg2 = leagueAverages.getOrDefault(p2.getLeague(), 1.0);
        calculator.calculateScores(p1, p2, avg1, avg2);

        String periodStr = DateTimeUtils.formatPeriod(start, end);

        return SalaryH2HResponseDto.builder()
                .player1(p1)
                .player2(p2)
                .winnerName(calculator.determineWinner(p1, p2))
                .period(periodStr)
                .build();
    }
}