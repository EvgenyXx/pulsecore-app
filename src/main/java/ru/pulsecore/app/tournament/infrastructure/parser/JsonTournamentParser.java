package ru.pulsecore.app.tournament.infrastructure.parser;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.TournamentPage;

@Slf4j
@Component
public class JsonTournamentParser {

    /**
     * Парсит страницу ОДИН раз и возвращает immutable-объект с готовыми полями.
     * Никакого состояния в бине, никакого @Scope("prototype").
     */
    public TournamentPage parse(Document doc) {
        JsonNode root = BootstrapJson.parse(doc);
        if (root == null) return null;

        return new TournamentPage(
                doc,
                root,
                root.path("tourId").asLong(),
                root.path("date").asText(null),
                root.path("time").asText(null),
                root.path("hallTitle").asText(null),
                root.path("leagueTitle").asText(null),
                root.path("typeId").asText(null),
                findRemovedPlayer(root)
        );
    }

    private String findRemovedPlayer(JsonNode root) {
        for (JsonNode p : root.path("players")) {
            if (p.path("removed").asBoolean(false)) {
                return p.path("name").asText(null);
            }
        }
        return null;
    }
}