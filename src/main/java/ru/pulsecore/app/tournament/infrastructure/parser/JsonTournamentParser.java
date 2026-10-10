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

        TournamentJson json;
        try {
            json = objectMapper.treeToValue(root, TournamentJson.class);
        } catch (JsonProcessingException e) {
            log.warn("Ошибка маппинга: {}", e.getMessage());
            return null;
        }

        return new TournamentPage(doc, json);
    }

}