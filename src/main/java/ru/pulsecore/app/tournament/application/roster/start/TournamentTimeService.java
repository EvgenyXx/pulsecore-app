package ru.pulsecore.app.tournament.application.roster.start;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
public class TournamentTimeService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Moscow");

    public boolean isStartedByTime(TournamentEntity t) {
        if (t.getDate() == null || t.getTime() == null) return false;

        ZonedDateTime start = ZonedDateTime.of(
                t.getDate(),
                LocalTime.parse(t.getTime()),
                ZONE
        );

        boolean started = ZonedDateTime.now(ZONE).isAfter(start);
        log.debug("Старт: турнир={}, время={}, начался={}",
                t.getExternalId(), t.getTime(), started);
        return started;
    }

    public boolean isToday(TournamentEntity t) {
        return t.getDate() != null &&
                t.getDate().isEqual(ZonedDateTime.now(ZONE).toLocalDate());
    }
}