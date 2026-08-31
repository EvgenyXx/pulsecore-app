package ru.pulsecore.app.admin.client;

public interface SchedulerClient {

    void pauseScheduler();
    void resumeScheduler();
    boolean isSchedulerPaused();
}
