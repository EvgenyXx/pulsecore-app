package ru.pulsecore.app.tournament.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MatchStatus {

    COMPLETED("completed"),
    CANCELED("canceled"),
    UNKNOWN("");

    private final String code;

    public static MatchStatus fromCode(String code) {
        if (code == null || code.isBlank()) return UNKNOWN;
        return Arrays.stream(values())
                .filter(s -> s.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(UNKNOWN);
    }
}