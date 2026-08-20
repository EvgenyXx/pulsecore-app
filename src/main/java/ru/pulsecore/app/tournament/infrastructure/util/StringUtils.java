package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    //todo вынести в шаред
    public static String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+");
        String firstName = parts.length >= 2 ? parts[1] : parts[0];
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
    }

    public static String capitalize(String name) {
        if (name == null || name.isBlank()) return name;
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    public static String normalizeSearch(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replace("\u00A0", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}