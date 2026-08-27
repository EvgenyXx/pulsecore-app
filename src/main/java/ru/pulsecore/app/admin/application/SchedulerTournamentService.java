package ru.pulsecore.app.admin.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.admin.client.SchedulerClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerTournamentService {

    private final SchedulerClient schedulerClient;

    public void pause() {
        schedulerClient.pauseScheduler();
        log.info("Планировщик приостановлен через админку");
    }

    public void resume() {
        schedulerClient.resumeScheduler();
        log.info("Планировщик возобновлён через админку");
    }

    public boolean isPaused() {
        return schedulerClient.isSchedulerPaused();
    }
}