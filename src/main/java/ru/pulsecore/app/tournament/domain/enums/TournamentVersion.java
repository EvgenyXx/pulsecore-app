package ru.pulsecore.app.tournament.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum TournamentVersion {

    /**
     * Стандартный формат турнира (typeId = "standart" или null/пусто).
     * Одна модель расчёта: DEFAULT / REMOVED.
     */
    STANDARD("standart"),

    /**
     * Новый формат 4pl (typeId = "4pl_new").
     * Модель расчёта: FOUR_PL_BRACKET / FOUR_PL_BRACKET_REMOVED.
     */
    FOUR_PL_NEW("4pl_new");

    private final String typeId;

    public static Optional<TournamentVersion> fromTypeId(String typeId) {
        if (typeId == null || typeId.isBlank()) {
            return Optional.of(FOUR_PL_NEW);
        }

        return Arrays.stream(values())
                .filter(v -> typeId.equals(v.typeId))
                .findFirst();
    }
}