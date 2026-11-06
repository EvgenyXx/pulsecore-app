package ru.pulsecore.app.tournament.application.lineup;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.config.AsyncConfig;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AsyncLineupLoader {

    private final LineupUpsertService lineupUpsertService;

    @Async(AsyncConfig.LINEUP_EXECUTOR)
    public void loadDayAsync(LocalDate date) {
        lineupUpsertService.loadDay(date);
    }
}