package ru.pulsecore.app.tournament.infrastructure.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.admin.client.SchedulerClient;
import ru.pulsecore.app.tournament.application.admin.SchedulerPauseService;

@Service
@RequiredArgsConstructor
public class SchedulerClientImpl implements SchedulerClient {

    private final SchedulerPauseService schedulerPauseService;

    @Override
    public void pauseScheduler() {
        schedulerPauseService.pause();
    }

    @Override
    public void resumeScheduler() {
        schedulerPauseService.resume();
    }

    @Override
    public boolean isSchedulerPaused() {
        return schedulerPauseService.isPaused();
    }
}