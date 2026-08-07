package ru.pulsecore.app.tournament.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.application.finish.TournamentFinishProcessor;




@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentFinishScheduler {

    private final TournamentFinishProcessor tournamentFinishProcessor;

    @Scheduled(cron = "0 */7 * * * *")
    public void checkFinished() {
        tournamentFinishProcessor.processAll();
    }
}