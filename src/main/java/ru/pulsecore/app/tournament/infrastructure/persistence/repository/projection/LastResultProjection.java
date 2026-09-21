package ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection;

public interface LastResultProjection {

    String getDate();

    double getAmount();

    Long getResultId();
}
