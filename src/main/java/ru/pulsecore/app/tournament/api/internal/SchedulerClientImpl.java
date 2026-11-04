package ru.pulsecore.app.tournament.api.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.api.tournament.SchedulerAdminClient;
import ru.pulsecore.app.tournament.application.admin.SchedulerPauseService;
import ru.pulsecore.app.tournament.application.roster.canceled.TournamentCanceledProcessor;
import ru.pulsecore.app.tournament.application.roster.change.TournamentChangeService;
import ru.pulsecore.app.tournament.application.roster.discovery.TournamentDiscoveryService;
import ru.pulsecore.app.tournament.application.roster.finish.TournamentFinishProcessor;
import ru.pulsecore.app.tournament.application.roster.start.TournamentStartProcessor;


@Service
@RequiredArgsConstructor
public class SchedulerClientImpl implements SchedulerAdminClient {

    private final SchedulerPauseService schedulerPauseService;
    private final TournamentFinishProcessor tournamentFinishProcessor;
    private final TournamentDiscoveryService discoveryService;
    private final TournamentStartProcessor startProcessor;
    private final TournamentCanceledProcessor processCanceled;
    private final TournamentChangeService changeService;


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

    @Override
    public void checkStart() {
        startProcessor.checkStart();
    }

    @Override
    public void processFinishedTournaments() {
        tournamentFinishProcessor.processFinish();
    }

    @Override
    public void checkNewTournaments() {
        discoveryService.checkNewTournaments();
    }

    @Override
    public void checkChangedTournament() {
        changeService.checkChangedTournaments();
    }

    @Override
    public void checkCanceled() {
        processCanceled.processCanceled();
    }
}