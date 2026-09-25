package ru.pulsecore.app.tournament.infrastructure.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Slf4j
public final class BootstrapJson {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String SELECTOR = "script#ml-tour-bootstrap";

    private BootstrapJson() {}

   public static JsonNode parse(Document doc) {
    if (doc == null) return null;
    Element script = doc.selectFirst(SELECTOR);
    if (script == null) return null;
    try {
        JsonNode root = MAPPER.readTree(script.data());
        log.debug("BootstrapJson: parsed tree:\n{}", root.toPrettyString());
        return root;
    } catch (Exception e) {
        log.warn("BootstrapJson: ошибка {}", e.getMessage());
        return null;
    }
}

//    public static String str(Document doc, String field) {
//        JsonNode root = parse(doc);
//        return root == null ? null : root.path(field).asText(null);
//    }
//
//    public static Long asLong(Document doc, String field) {
//        JsonNode root = parse(doc);
//        return root == null ? null : root.path(field).asLong();
//    }
}