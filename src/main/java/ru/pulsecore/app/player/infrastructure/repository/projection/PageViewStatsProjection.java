package ru.pulsecore.app.player.infrastructure.repository.projection;

public interface PageViewStatsProjection {
    String getPath();
    String getMethod();
    long getCount();
}