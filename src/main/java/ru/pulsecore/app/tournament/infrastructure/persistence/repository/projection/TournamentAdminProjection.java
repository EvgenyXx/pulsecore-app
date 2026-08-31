package ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection;

import java.time.LocalDate;
import java.util.List;

public interface TournamentAdminProjection {
    Long getId();
    String getLink();
    LocalDate getDate();
    String getTime();
    boolean getStarted();
    boolean getFinished();
    boolean getCancelled();
    boolean getProcessed();
    String getPlayers();
}