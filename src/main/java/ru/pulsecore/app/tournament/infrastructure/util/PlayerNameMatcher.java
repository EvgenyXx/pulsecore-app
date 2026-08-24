package ru.pulsecore.app.tournament.infrastructure.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PlayerNameMatcher {

    /**
     * Сравнивает два имени игроков, приводя их к единому формату.
     *
     * Алгоритм:
     * 1. Оба имени приводятся к нижнему регистру, убираются спецсимволы
     * 2. Имена разбиваются на слова
     * 3. Считается сколько слов совпало
     * 4. Если совпало 2+ слова — считаем что это один и тот же игрок
     *
     * Примеры:
     * isSamePlayer("Хрипуненко Павел", "хрипуненко павел") → true (2 совпадения)
     * isSamePlayer("Хрипуненко Павел Иванович", "Павел Хрипуненко") → true (2 совпадения)
     * isSamePlayer("Хрипуненко Павел", "Чернов Александр") → false (0 совпадений)
     * isSamePlayer("Карпенко Вячеслав", "карпенко вячеслав") → true (2 совпадения)
     */
    public static boolean isSamePlayer(String n1, String n2) {
        if (n1 == null || n2 == null) return false;

        // Приводим к нижнему регистру и убираем лишнее
        String p1 = normalizeName(n1);
        String p2 = normalizeName(n2);

        // Разбиваем на слова
        String[] parts1 = p1.split(" ");
        String[] parts2 = p2.split(" ");

        // Считаем совпадения слов
        int matches = 0;
        for (String part1 : parts1) {
            for (String part2 : parts2) {
                if (part1.equals(part2)) matches++;
            }
        }

        // 2+ совпадения — один и тот же игрок
        return matches >= 2;
    }

    /**
     * Приводит имя к нижнему регистру, убирает спецсимволы, лишние пробелы.
     *
     * Примеры:
     * normalizeName("Хрипуненко Павел") → "хрипуненко павел"
     * normalizeName("ЧЕРНОВ АЛЕКСАНДР (снялся)") → "чернов александр снялся"
     */
    private static String normalizeName(String name) {
        return name.toLowerCase()
                .replaceAll("[^а-яa-z\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
}