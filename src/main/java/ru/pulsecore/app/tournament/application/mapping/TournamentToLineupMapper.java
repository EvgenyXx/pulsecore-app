package ru.pulsecore.app.tournament.application.mapping;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.entity.Lineup;
import ru.pulsecore.app.tournament.domain.enums.LineupType;

import java.time.LocalDate;

@Component
public class TournamentToLineupMapper {

    public Lineup toEntity(TournamentDto t, LocalDate date, String time) {
        return Lineup.builder()
                .league(t.getLeague())
                .time(time)
                .hall(t.getHall())
                .players(String.join(", ", t.getPlayers()))
                .date(date)
                .link(t.getLink())
                .type(LineupType.fromApiType(t.getType()))
                .build();
    }
}