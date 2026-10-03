package ru.pulsecore.app.tournament.infrastructure.parser;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.api.dto.TournamentJson;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.model.Match;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JsonMatchParser {

    public List<Match> parseMatches(TournamentPage page) {
        List<Match> result = new ArrayList<>();


        TournamentJson json = page.json();
        for (TournamentJson.GameJson gameJson :  json.games()) {
            Match m = parseGame(gameJson);
            if (m != null) result.add(m);
        }
        return result;
    }

   private Match parseGame(TournamentJson.GameJson gameJson) {
    try {
        String p1 = gameJson.player1().name();
        String p2 = gameJson.player2().name();

        Match m = new Match();
        m.setStage(gameJson.groupTitle());
        m.setStatus(gameJson.statusTitle());
        m.setPlayer1(p1);
        m.setPlayer2(p2);
        m.setScore1(gameJson.player1().result());
        m.setScore2(gameJson.player2().result());
        m.setSetsDetails(gameJson.setsStr());
        m.setTable(gameJson.hallTitle());
        m.setGroupType(gameJson.groupType());
        m.setSortNumber(gameJson.sortNumber());

        return m;
    } catch (Exception e) {
        log.warn("JsonMatchParser: ошибка матча {}", e.getMessage());
        return null;
    }
}
}