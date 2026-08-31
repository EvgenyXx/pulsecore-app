package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class NameNormalizer {

    private static final Pattern BRACKETS = Pattern.compile("\\(.*?\\)");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

    /**
     * Нормализует имя: убирает скобки, точки, лишние пробелы.
     *
     * Примеры:
     * normalize("Чернов Александр (снялся)") → "Чернов Александр"
     * normalize("Иванов. Петр") → "Иванов Петр"
     * normalize("  Петров   Евгений  ") → "Петров Евгений"
     * normalize(null) → ""
     */
    public String normalize(String name) {
        if (name == null || name.isBlank()) return "";
        return MULTIPLE_SPACES.matcher(
                BRACKETS.matcher(name.replace("\u00A0", " ")).replaceAll("")
                        .replace("."," ")
        ).replaceAll(" ").trim();
    }

    /**
     * Нормализует список имён игроков.
     *
     * Пример:
     * normalizePlayers(List.of("Чернов Александр (снялся)", "Иванов. Петр"))
     * → ["Чернов Александр", "Иванов Петр"]
     */
    public static List<String> normalizePlayers(List<String> players) {
        return players == null ? null : players.stream().map(NameNormalizer::normalize).toList();
    }

    /**
     * Нормализует имя и приводит к нижнему регистру для поиска.
     *
     * Примеры:
     * normalizeForSearch("Чернов Александр") → "чернов александр"
     * normalizeForSearch("Иванов. Петр") → "иванов петр"
     * normalizeForSearch(null) → ""
     */
    public String normalizeForSearch(String name) {
        return name == null || name.isBlank() ? "" : normalize(name).toLowerCase();
    }
}