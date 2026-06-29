package ru.pulsecore.app.modules.shared.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public static String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        return fullName.contains(" ") ? fullName.substring(fullName.lastIndexOf(" ") + 1) : fullName;
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