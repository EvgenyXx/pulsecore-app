package ru.pulsecore.app.player.infrastructure.persistence.repository.projection;

public interface PageViewStatsProjection {
    String getPath();
    String getMethod();
    long getCount();
}