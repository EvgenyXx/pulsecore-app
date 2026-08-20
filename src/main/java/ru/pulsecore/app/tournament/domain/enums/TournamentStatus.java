package ru.pulsecore.app.tournament.domain.enums;

public enum TournamentStatus {
    NOT_STARTED,
    STARTED,
    IN_PROGRESS,
    FINISHED,
    CANCELLED;

    public boolean isFinished() {
        return this == FINISHED;
    }
}