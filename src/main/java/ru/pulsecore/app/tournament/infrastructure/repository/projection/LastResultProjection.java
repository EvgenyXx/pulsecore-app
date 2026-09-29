package ru.pulsecore.app.tournament.infrastructure.repository.projection;

public interface LastResultProjection {

    String getDate();

    double getAmount();

    Long getResultId();
}
