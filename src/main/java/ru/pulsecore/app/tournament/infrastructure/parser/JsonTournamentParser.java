package ru.pulsecore.app.tournament.infrastructure.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JsonTournamentParser {

    public Long parseTournamentId(Document doc) {
        return BootstrapJson.asLong(doc, "tourId");
    }

    public String parseDate(Document doc) {
        return BootstrapJson.str(doc, "date");
    }

    public String parseTime(Document doc) {
        return BootstrapJson.str(doc, "time");
    }

    public String parseHall(Document doc) {
        return BootstrapJson.str(doc, "hallTitle");
    }

    public String parseLeague(Document doc) {
        return BootstrapJson.str(doc, "leagueTitle");
    }

    public String findRemovedPlayer(Document doc) {
        JsonNode root = BootstrapJson.parse(doc);
        if (root == null) return null;
        for (JsonNode p : root.path("players")) {
            if (p.path("removed").asBoolean(false)) {
                return p.path("name").asText(null);
            }
        }
        return null;
    }
}