package ru.pulsecore.app.tournament.application.compare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.api.dto.response.ScoreStatsDto;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentMatchRepository;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.MatchScoreProjection;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreStatsService {

    private final TournamentMatchRepository matchRepository;

    public List<ScoreStatsDto> getScoreStats(String player1Name, String player2Name,
                                             LocalDateTime start, LocalDateTime end) {
        log.debug("Score stats requested: p1={}, p2={}, start={}, end={}", player1Name, player2Name, start, end);

        List<MatchScoreProjection> matches = fetchMatches(player1Name, player2Name, start, end);

        if (matches.isEmpty()) {
            log.debug("No matches found for score stats");
            return Collections.emptyList();
        }

        Map<String, Map<String, Long>> stats = accumulateByPlayer(matches, player1Name, player2Name);
        List<ScoreStatsDto> result = buildDtoList(stats, player1Name, player2Name);

        log.debug("Score stats calculated: {} unique scores", result.size());
        return result;
    }

    private List<MatchScoreProjection> fetchMatches(String player1Name, String player2Name,
                                                    LocalDateTime start, LocalDateTime end) {
        List<MatchScoreProjection> matches = matchRepository.findMatchScores(
                player1Name, player2Name, start, end
        );
        log.debug("Fetched {} matches from repository", matches != null ? matches.size() : 0);
        return matches != null ? matches : Collections.emptyList();
    }

    private Map<String, Map<String, Long>> accumulateByPlayer(List<MatchScoreProjection> matches,
                                                              String player1Name, String player2Name) {
        Map<String, Map<String, Long>> stats = new HashMap<>();
        stats.put(player1Name, new HashMap<>());
        stats.put(player2Name, new HashMap<>());

        for (MatchScoreProjection m : matches) {
            String rawScore = m.getScore();
            String winner = m.getWinnerName();

            if (rawScore == null || winner == null) {
                log.debug("Skipping match with null score or winner");
                continue;
            }

            String normalizedScore = normalizeScore(rawScore);
            if (normalizedScore == null) continue;

            String playerKey = resolvePlayerKey(winner, player1Name, player2Name);
            if (playerKey != null) {
                incrementScore(stats.get(playerKey), normalizedScore);
            }
        }

        return stats;
    }

    private String normalizeScore(String rawScore) {
        String[] parts = rawScore.split(":");
        if (parts.length != 2) return null;

        try {
            int a = Integer.parseInt(parts[0].trim());
            int b = Integer.parseInt(parts[1].trim());
            int loserScore = Math.min(a, b);
            return "4:" + loserScore;
        } catch (NumberFormatException e) {
            log.debug("Could not parse score: {}", rawScore);
            return null;
        }
    }

    private String resolvePlayerKey(String winner, String player1Name, String player2Name) {
        if (winner.equalsIgnoreCase(player1Name)) return player1Name;
        if (winner.equalsIgnoreCase(player2Name)) return player2Name;
        log.debug("Winner '{}' is not one of the compared players", winner);
        return null;
    }

    private void incrementScore(Map<String, Long> scores, String score) {
        scores.put(score, scores.getOrDefault(score, 0L) + 1);
    }

    private List<ScoreStatsDto> buildDtoList(Map<String, Map<String, Long>> stats,
                                             String player1Name, String player2Name) {
        Set<String> allScores = collectUniqueScores(stats, player1Name, player2Name);

        return allScores.stream()
                .map(score -> buildDto(score, stats, player1Name, player2Name))
                .sorted(Comparator.comparing(ScoreStatsDto::getScore).reversed())
                .collect(Collectors.toList());
    }

    private Set<String> collectUniqueScores(Map<String, Map<String, Long>> stats,
                                            String player1Name, String player2Name) {
        Set<String> allScores = new HashSet<>();
        allScores.addAll(stats.getOrDefault(player1Name, Collections.emptyMap()).keySet());
        allScores.addAll(stats.getOrDefault(player2Name, Collections.emptyMap()).keySet());
        return allScores;
    }

    private ScoreStatsDto buildDto(String score, Map<String, Map<String, Long>> stats,
                                   String player1Name, String player2Name) {
        return ScoreStatsDto.builder()
                .score(score)
                .player1Wins(stats.getOrDefault(player1Name, Collections.emptyMap()).getOrDefault(score, 0L))
                .player2Wins(stats.getOrDefault(player2Name, Collections.emptyMap()).getOrDefault(score, 0L))
                .build();
    }
}