package ru.pulsecore.app.tournament.domain.enums;

import lombok.Getter;

@Getter
public enum GameStatus {
    PLANNED("planned"),
    GOES("goes"),
    COMPLETED("completed"),
    CANCELED("canceled");

    private final String code;

    GameStatus(String code) { this.code = code; }

    public static GameStatus fromCode(String code) {
        for (GameStatus s : values()) {
            if (s.code.equals(code)) return s;
        }
        return null;
    }
}