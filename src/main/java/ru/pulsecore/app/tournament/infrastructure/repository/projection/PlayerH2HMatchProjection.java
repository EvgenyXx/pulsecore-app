package ru.pulsecore.app.tournament.infrastructure.repository.projection;

import java.time.LocalDateTime;

public interface PlayerH2HMatchProjection {
    LocalDateTime getPlayedAt();
    String getScore();
    String getWinnerName();
}