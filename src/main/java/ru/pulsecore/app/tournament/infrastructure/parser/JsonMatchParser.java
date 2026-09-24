package ru.pulsecore.app.tournament.infrastructure.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.model.Match;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonMatchParser {

    public List<Match> parseMatches(Document doc) {
        List<Match> result = new ArrayList<>();
        JsonNode root = BootstrapJson.parse(doc);
        if (root == null) return result;

        for (JsonNode g : root.path("games")) {
            Match m = parseGame(g);
            if (m != null) result.add(m);
        }
        return result;
    }

    private Match parseGame(JsonNode g) {
        try {
            JsonNode p1 = g.path("player1");
            JsonNode p2 = g.path("player2");

            Match m = new Match();
            m.setStage(g.path("groupTitle").asText(null));
            m.setStatus(g.path("statusTitle").asText(null));
            m.setPlayer1(p1.path("name").asText(null));
            m.setPlayer2(p2.path("name").asText(null));
            m.setScore1(p1.path("result").asInt(0));
            m.setScore2(p2.path("result").asInt(0));
            m.setSetsDetails(g.path("setsStr").asText(null));
            m.setTable(g.path("hallTitle").asText(null));
            return m;
        } catch (Exception e) {
            log.warn("JsonMatchParser: ошибка матча {}", e.getMessage());
            return null;
        }
    }
}