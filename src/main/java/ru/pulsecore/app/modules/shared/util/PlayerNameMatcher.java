package ru.pulsecore.app.modules.shared.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PlayerNameMatcher {

    public static boolean isSamePlayer(String n1, String n2) {
        if (n1 == null || n2 == null) return false;

        String p1 = normalizeName(n1);
        String p2 = normalizeName(n2);
        String[] parts1 = p1.split(" ");
        String[] parts2 = p2.split(" ");

        int matches = 0;
        for (String part1 : parts1) {
            for (String part2 : parts2) {
                if (part1.equals(part2)) matches++;
            }
        }
        return matches >= 2;
    }

    private static String normalizeName(String name) {
        return name.toLowerCase()
                .replaceAll("[^а-яa-z\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
}