package ru.pulsecore.app.tournament.infrastructure.repository.projection;

import java.time.LocalDate;
import java.util.List;

public interface TournamentAdminProjection {
    Long getId();
    String getLink();
    LocalDate getDate();
    String getTime();
    Boolean getStarted();
    Boolean getFinished();
    Boolean getCancelled();
    Boolean getProcessed();
    String getPlayers();
    String getEarnings();   // ← новое
}