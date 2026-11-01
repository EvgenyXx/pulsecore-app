package ru.pulsecore.app.shared.api.tournament;

public interface SchedulerAdminClient {

    void pauseScheduler();
    void resumeScheduler();
    boolean isSchedulerPaused();
}
