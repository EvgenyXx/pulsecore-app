// src/main/java/ru/pulsecore/app/modules/tournament/api/dto/WeeklyStatsProjection.java
package ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection;

;

public interface WeeklyStatsProjection {
    String getName();
    Long getTournaments();
    Double getTotal();
    Double getAverage();
}