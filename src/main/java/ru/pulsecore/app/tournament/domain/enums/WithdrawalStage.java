package ru.pulsecore.app.tournament.domain.enums;

public enum WithdrawalStage {

    /**
     * Снятие в играх 1–6.
     */
    BEFORE_PLAYOFF,

    /**
     * Снятие в игре 7 (полуфинал).
     */
    PLAYOFF,

    /**
     * ни кто не снялся
     */
    NONE
}