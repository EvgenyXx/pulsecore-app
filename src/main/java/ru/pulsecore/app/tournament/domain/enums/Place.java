package ru.pulsecore.app.tournament.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Place {
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4);

    private final int value;
}