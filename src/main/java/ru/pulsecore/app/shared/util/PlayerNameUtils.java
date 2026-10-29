package ru.pulsecore.app.shared.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.stream.Collectors;

@UtilityClass
public class PlayerNameUtils {

    public String cleanName(String name) {
        if (name == null || name.isBlank()) return "";
        return Arrays.stream(name.toLowerCase().trim().split("\\s+"))
                .map(t -> RegexUtils.ONLY_LETTERS.matcher(t).replaceAll(""))
                .filter(t -> !t.isEmpty())
                .collect(Collectors.joining(" "));
    }

    /**
     * Нормализует имя для хранения и сравнения: приводит к нижнему регистру,
     * обрезает пробелы по краям, схлопывает повторные пробелы и сортирует слова
     * по алфавиту, чтобы порядок «фамилия имя» и «имя фамилия» давал одинаковый результат.
     * Примеры:
     * normalizeName("Хрипуненко Павел") → "павел хрипуненко"
     * normalizeName("  ЧЕРНОВ   АЛЕКСАНДР ") → "александр чернов"
     * normalizeName(null) → ""
     */
     public String normalizeName(String name) {
        if (name == null || name.isBlank()) return "";
        return Arrays.stream(name.toLowerCase().trim().split("\\s+"))
                .map(t -> RegexUtils.ONLY_LETTERS.matcher(t).replaceAll(""))
                .filter(t -> !t.isEmpty())
                .sorted()
                .collect(Collectors.joining(" "));
    }

    /**
     * Извлекает первое имя из полного ФИО и делает его с заглавной буквы.
     * Примеры:
     * extractFirstName("хрипуненко павел") → "Павел"
     * extractFirstName("ЧЕРНОВ АЛЕКСАНДР ИВАНОВИЧ") → "Александр"
     * extractFirstName("карпенко") → "Карпенко"
     */
    public String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+");
        String firstName = parts.length >= 2 ? parts[1] : parts[0];
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
    }

    /**
     * Делает каждое слово с заглавной буквы, остальные буквы — строчные.
     * Примеры:
     * capitalize("хрипуненко павел") → "Хрипуненко Павел"
     * capitalize("ЧЕРНОВ АЛЕКСАНДР") → "Чернов Александр"
     * capitalize("карпенко") → "Карпенко"
     */
    public String capitalize(String name) {
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

    /**
     * Возвращает фамилию + инициалы с больших букв.
     * Примеры:
     * shortName("хрипуненко павел") → "Хрипуненко П."
     * shortName("ЧЕРНОВ АЛЕКСАНДР ИВАНОВИЧ") → "Чернов А. И."
     * shortName("карпенко") → "Карпенко"
     */
    public String shortName(String fullName) {
        if (fullName == null || fullName.isBlank()) return fullName;
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return capitalize(parts[0]);

        // Фамилия + инициалы
        StringBuilder sb = new StringBuilder();
        sb.append(capitalize(parts[0]));

        for (int i = 1; i < parts.length; i++) {
            sb.append(" ");
            sb.append(Character.toUpperCase(parts[i].charAt(0)));
            sb.append(".");
        }

        return sb.toString();
    }

    /**
     * Приводит имя к нижнему регистру для поиска и сравнения.
     * Убирает неразрывные пробелы и лишние пробелы.
     * Примеры:
     * normalizeSearch("Хрипуненко Павел") → "хрипуненко павел"
     * normalizeSearch("ЧЕРНОВ  АЛЕКСАНДР") → "чернов александр"
     * normalizeSearch(" Карпенко ") → "карпенко"
     */
    public String normalizeSearch(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replace("\u00A0", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}