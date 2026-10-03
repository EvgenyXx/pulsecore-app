package ru.pulsecore.app.tournament.infrastructure.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.api.dto.TournamentJson;
import ru.pulsecore.app.tournament.domain.TournamentPage;

@Slf4j
@Component
public class JsonTournamentParser {

    private final ObjectMapper objectMapper;

    public JsonTournamentParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Парсит страницу ОДИН раз и возвращает immutable-объект с готовыми полями.
     * Никакого состояния в бине, никакого @Scope("prototype").
     */
    public TournamentPage parse(Document doc) {
        JsonNode root = BootstrapJson.parse(doc);
        if (root == null) return null;

        TournamentJson json = parserJson(root);



        return new TournamentPage(
                doc, root,
                json.tourId(),
                json.date(),
                json.time(),
                json.hallTitle(),
                json.leagueTitle(),
                json.typeId(),
                findRemovedPlayer(json)
                );
//        return new TournamentPage(
//                doc,
//                root,
//                root.path("tourId").asLong(),
//                root.path("date").asText(null),
//                root.path("time").asText(null),
//                root.path("hallTitle").asText(null),
//                root.path("leagueTitle").asText(null),
//                root.path("typeId").asText(null),
//                findRemovedPlayer(root)
//        );
    }

    private TournamentJson parserJson(JsonNode jsonNode) {
        try {
            return objectMapper.treeToValue(jsonNode, TournamentJson.class);
        } catch (JsonProcessingException e) {
            log.warn("Не удалось замапить JSON турнира: {}", e.getMessage());
            return null;
        }
    }

    private String findRemovedPlayer(TournamentJson json) {
        if (json.players() == null) {
            return null;
        }
        return json.players().stream()
                .filter(p -> Boolean.TRUE.equals(p.removed()))
                .map(TournamentJson.PlayerJson::name)
                .findFirst()
                .orElse(null);
    }

//    private String findRemovedPlayer(JsonNode root) {
//        for (JsonNode p : root.path("players")) {
//            if (p.path("removed").asBoolean(false)) {
//                return p.path("name").asText(null);
//            }
//        }
//        return null;
//    }
}