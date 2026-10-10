package ru.pulsecore.app.tournament.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MatchNumber {

    TOP_MESH_1_GAME_1(1),
    TOP_MESH_1_GAME_2(2),
    LOWER_MESH_1(3),
    TOP_MESH_2(4),
    LOWER_MESH_2(5),
    LOWER_MESH_3_GAME_1(6),
    LOWER_MESH_3_GAME_2(7),
    FINAL(8);

    /** Последняя игра, где снятие классифицируется как BEFORE_PLAYOFF. */
    private static final int LAST_EARLY_NUMBER = 6;

    /** Игра за 3-е место (полуфинал). */
    private static final MatchNumber SEMIFINAL = LOWER_MESH_3_GAME_2;

    private final int number;

    MatchNumber(int number) {
        this.number = number;
    }

    public boolean isEarly() {
        return number <= LAST_EARLY_NUMBER;
    }

    public boolean isSemifinal() {
        return this == SEMIFINAL;
    }

    public static MatchNumber fromNumber(int number) {
        return Arrays.stream(values())
                .filter(m -> m.number == number)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown match number: " + number));
    }
}