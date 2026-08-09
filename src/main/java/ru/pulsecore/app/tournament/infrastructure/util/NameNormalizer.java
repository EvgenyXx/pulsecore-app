package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class NameNormalizer {

    private static final Pattern BRACKETS = Pattern.compile("\\(.*?\\)");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

    public String normalize(String name) {
        if (name == null || name.isBlank()) return "";
        return MULTIPLE_SPACES.matcher(
                BRACKETS.matcher(name.replace("\u00A0", " ")).replaceAll("")
        ).replaceAll(" ").trim();
    }
    public static List<String> normalizePlayers(List<String> players) {
        return players == null ? null : players.stream().map(NameNormalizer::normalize).toList();
    }
    public String normalizeForSearch(String name) {
        return name == null || name.isBlank() ? "" : normalize(name).toLowerCase();
    }
}