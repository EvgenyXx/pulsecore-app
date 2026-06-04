package ru.pulsecore.app.modules.shared.service;

import org.springframework.stereotype.Component;
import java.util.List;


@Component
public class NameNormalizer {

    /**
     * Нормализует одно имя:
     * - убирает пробелы в начале и конце
     * - заменяет все множественные пробелы на один
     * - убирает неразрывные пробелы
     * - удаляет текст в скобках (снят, удален и т.п.)
     */
    public String normalize(String name) {
        if (name == null || name.isBlank()) return "";

        return name
                .replace("\u00A0", " ")           // неразрывный пробел → обычный
                .replaceAll("\\(.*?\\)", "")      // удаляем (снят) и т.п.
                .trim()                           // убираем пробелы по краям
                .replaceAll("\\s+", " ");         // все пробелы → один
    }

    /**
     * Нормализует список имён
     */
    public List<String> normalizePlayers(List<String> players) {
        if (players == null) return null;
        return players.stream()
                .map(this::normalize)
                .toList();
    }

    /**
     * Нормализует имя для поиска (в нижний регистр)
     */
    public String normalizeForSearch(String name) {
        if (name == null || name.isBlank()) return "";
        return normalize(name).toLowerCase();
    }
}