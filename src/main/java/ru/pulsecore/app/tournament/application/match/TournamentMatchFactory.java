package ru.pulsecore.app.tournament.application.match;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.domain.enums.MatchStage;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.infrastructure.exception.StageParserNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentMatchFactory {

    private final StageParserResolver stageParserResolver;

    public TournamentMatchEntity create(TournamentEntity tournament, Match m, LocalDateTime playedAt) {
        log.debug("Создание матча: tournament_id={}, typeId={}, stage={}, p1='{}', p2='{}', winner='{}'",
                tournament.getId(), m.getTypeId(), m.getStage(), m.getPlayer1(), m.getPlayer2(), m.winnerOf());

        String player1Name = NameNormalizer.normalize(m.getPlayer1().toLowerCase());
        String player2Name = NameNormalizer.normalize(m.getPlayer2().toLowerCase());
        String winnerName = NameNormalizer.normalize(m.winnerOf().toLowerCase());

        TournamentMatchEntity entity = new TournamentMatchEntity();
        entity.setTournament(tournament);
        entity.setPlayer1Name(player1Name);
        entity.setPlayer2Name(player2Name);
        entity.setWinnerName(winnerName);
        try {
            StageParser stageParser = stageParserResolver.resolve(m.getTypeId());
            entity.setStage(stageParser.parse(m.getStage()));
        } catch (StageParserNotFoundException e) {
            log.warn(e.getMessage());
            entity.setStage(MatchStage.GROUP);
        }
        entity.setScore(m.getScore1() + ":" + m.getScore2());
        entity.setPlayedAt(playedAt);
        return entity;
    }


}