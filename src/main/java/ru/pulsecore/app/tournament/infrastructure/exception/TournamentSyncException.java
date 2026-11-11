package ru.pulsecore.app.tournament.infrastructure.exception;

import ru.pulsecore.app.shared.exception.BaseException;

public class TournamentSyncException extends BaseException {
    public TournamentSyncException(String message) {
        super(message);
    }
}