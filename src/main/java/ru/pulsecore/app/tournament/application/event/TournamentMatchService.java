package ru.pulsecore.app.tournament.application.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.domain.entity.MatchStage;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentMatchRepository;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentMatchService {

    private final TournamentMatchRepository matchRepository;

    @Transactional
    public void createMatches(ParsedResult parsed, TournamentEntity tournament) {
        if (parsed == null || parsed.matches() == null || parsed.matches().isEmpty()) {
            log.debug("Матчи: нет данных для сохранения");
            return;
        }

        // Если турнир уже есть — пропускаем
        if (matchRepository.existsByTournamentId(tournament.getId())) {
            log.debug("Матчи: турнир {} уже существует, пропуск", parsed.tournamentId());
            return;
        }

        List<TournamentMatchEntity> matches = new ArrayList<>();
        LocalDateTime playedAt = parseDate(parsed.date());

        for (Match m : parsed.matches()) {
            if (m.getStatus() == null || !m.getStatus().toLowerCase().contains("заверш")) continue;

            String player1Name = NameNormalizer.normalize(m.getPlayer1());
            String player2Name = NameNormalizer.normalize(m.getPlayer2());
            String winnerName = m.getScore1() > m.getScore2() ? player1Name : player2Name;

            TournamentMatchEntity entity = new TournamentMatchEntity();
            entity.setTournament(tournament);
            entity.setPlayer1Name(player1Name);
            entity.setPlayer2Name(player2Name);
            entity.setWinnerName(winnerName);
            entity.setStage(parseStage(m.getStage()));
            entity.setScore(m.getScore1() + ":" + m.getScore2());
            entity.setPlayedAt(playedAt);
            matches.add(entity);
        }

        if (!matches.isEmpty()) {
            matchRepository.saveAll(matches);
            log.info("Матчи: сохранено={} для турнира={}", matches.size(), parsed.tournamentId());
        }
    }

    private MatchStage parseStage(String stage) {
        if (stage == null) return MatchStage.GROUP;
        String s = stage.toLowerCase();
        if (s.contains("финал") && !s.contains("1/2") && !s.contains("за 3")) return MatchStage.FINAL;
        if (s.contains("1/2") || s.contains("полуфинал")) return MatchStage.SEMIFINAL;
        if (s.contains("за 3") || s.contains("3-е") || s.contains("третье")) return MatchStage.THIRD_PLACE;
        return MatchStage.GROUP;
    }

    private LocalDateTime parseDate(String date) {
        if (date == null) return LocalDateTime.now();
        try {
            return LocalDate.parse(date).atStartOfDay();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}