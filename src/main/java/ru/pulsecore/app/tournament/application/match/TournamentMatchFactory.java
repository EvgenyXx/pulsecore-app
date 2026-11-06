package ru.pulsecore.app.tournament.application.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.entity.MatchStage;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TournamentMatchFactory {

    public TournamentMatchEntity create(TournamentEntity tournament, Match m, LocalDateTime playedAt) {
        String player1Name = NameNormalizer.normalize(m.getPlayer1());
        String player2Name = NameNormalizer.normalize(m.getPlayer2());

        TournamentMatchEntity entity = new TournamentMatchEntity();
        entity.setTournament(tournament);
        entity.setPlayer1Name(player1Name);
        entity.setPlayer2Name(player2Name);
        entity.setWinnerName(m.winnerOf());
        entity.setStage(parseStage(m.getStage()));
        entity.setScore(m.getScore1() + ":" + m.getScore2());
        entity.setPlayedAt(playedAt);
        return entity;
    }

    private MatchStage parseStage(String stage) {
        if (stage == null) return MatchStage.GROUP;
        String s = stage.toLowerCase();
        if (s.contains("финал") && !s.contains("1/2") && !s.contains("за 3")) return MatchStage.FINAL;
        if (s.contains("1/2") || s.contains("полуфинал")) return MatchStage.SEMIFINAL;
        if (s.contains("за 3") || s.contains("3-е") || s.contains("третье")) return MatchStage.THIRD_PLACE;
        return MatchStage.GROUP;
    }
}