package ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection;

import java.time.LocalDate;

public interface TournamentProjection {
    Long getId();
    Long getExternalId();
    String getLink();
    LocalDate getDate();
    String getTime();
    Integer getHall();
}