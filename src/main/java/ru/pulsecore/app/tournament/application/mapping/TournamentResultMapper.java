package ru.pulsecore.app.tournament.application.mapping;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.response.ResultDto;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class TournamentResultMapper {

    public TournamentResultEntity buildEntity(UUID playerId, TournamentEntity tournament, ResultDto r,
                                               double bonus, boolean hasRemoved, String league) {
        return TournamentResultEntity.builder()
                .playerId(playerId)
                .playerName(r.getPlayer())
                .amount((double) r.getTotal())
                .date(LocalDate.parse(r.getDate()))
                .tournament(tournament)
                .isNight(bonus > 0)
                .bonus(bonus)
                .hasRemoved(hasRemoved)
                .league(league)
                .build();
    }
}