package ru.pulsecore.app.tournament.application.compare.assembler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.api.dto.response.SalaryH2HPlayerDto;
import ru.pulsecore.app.tournament.infrastructure.exception.PlayerStatsNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PlayerCompareResponse;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PlayerMatchStatsProjection;

import java.util.List;

/**
 * Ассемблер для сборки DTO игрока из данных о деньгах и матчах.
 * Отвечает за объединение данных из разных источников в один объект.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SalaryH2HAssembler {

    private static final double PERCENT_MULTIPLIER = 100.0;
    private static final double ROUNDING_FACTOR = 10.0;

    /**
     * Собирает DTO для конкретного игрока на основе статистики турниров и матчей.
     *
     * @param name       имя игрока
     * @param moneyStats список данных о заработке за период
     * @param matchStats список данных о матчах за период
     * @return готовый DTO игрока
     * @throws PlayerStatsNotFoundException если данных нет ни по деньгам, ни по матчам
     */
    public SalaryH2HPlayerDto assemblePlayerDto(String name,
                                                List<PlayerCompareResponse> moneyStats,
                                                List<PlayerMatchStatsProjection> matchStats) {

        PlayerCompareResponse money = findMoneyStats(name, moneyStats);
        PlayerMatchStatsProjection match = findMatchStats(name, matchStats);

        long totalMatches = extractTotalMatches(match);
        long wins = extractWins(match);
        double winrate = calculateWinrate(wins, totalMatches);

        return SalaryH2HPlayerDto.builder()
                .name(name)
                .league(money.getPrimaryLeague())
                .tournamentsCount(money.getTournaments())
                .totalAmount(money.getTotalAmount())
                .avgAmount(money.getAverageAmount())
                .winratePercent(round(winrate))
                .build();
    }

    private PlayerCompareResponse findMoneyStats(String name, List<PlayerCompareResponse> moneyStats) {
        return moneyStats.stream()
                .filter(m -> m.getPlayerName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Нет данных о заработке для игрока: {}", name);
                    return new PlayerStatsNotFoundException(name);
                });
    }

    private PlayerMatchStatsProjection findMatchStats(String name, List<PlayerMatchStatsProjection> matchStats) {
        return matchStats.stream()
                .filter(m -> m.getPlayerName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Нет данных о матчах для игрока: {}", name);
                    return new PlayerStatsNotFoundException(name);
                });
    }

    private long extractTotalMatches(PlayerMatchStatsProjection match) {
        return match.getTotalMatches() != null ? match.getTotalMatches() : 0;
    }

    private long extractWins(PlayerMatchStatsProjection match) {
        return match.getWins() != null ? match.getWins() : 0;
    }

    private double calculateWinrate(long wins, long totalMatches) {
        if (totalMatches == 0) return 0.0;
        return ((double) wins / totalMatches) * PERCENT_MULTIPLIER;
    }

    private double round(double value) {
        return Math.round(value * ROUNDING_FACTOR) / ROUNDING_FACTOR;
    }
}