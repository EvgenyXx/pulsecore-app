package ru.pulsecore.app.tournament.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.LocalDate;

@ConfigurationProperties(prefix = "app.settings.tournament")
public record TournamentSettingProperties(
        Cleanup cleanup,
        PlayerHistory playerHistory,
        Cache cache
) {
    public record Cleanup(int start, int end) {
    }

    public record PlayerHistory(
            int start,
            LocalDate end
    ) {
    }

    public record Cache(
            Duration lineupHashTtl,
            Duration tournamentStatusTtl,
            Duration discoveryHashTtl
    ) {
    }
}