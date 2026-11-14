package ru.pulsecore.app.player.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum City {
    ROSTOV("Ростов"),
    ORENBURG("Оренбург"),
    PITER("Питер"),
    USSURIYSK("Уссурийск"),
    SARANSK("Саранск");

    private final String displayName;

    public static City fromHallNumber(Integer hallNumber) {

        return switch (hallNumber) {
            case 10, 11 -> ROSTOV;
            case 20, 21 -> ORENBURG;
            case 3, 4, 8, 9 -> PITER;
            case 5 -> USSURIYSK;
            default -> SARANSK;
        };
    }
}