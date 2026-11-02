package ru.pulsecore.app.tournament.application.mapping;

import org.mapstruct.*;
import ru.pulsecore.app.admin.api.dto.request.UpdateTournamentRequest;
import ru.pulsecore.app.shared.dto.response.AdminTournamentResponse;
import ru.pulsecore.app.shared.dto.response.PriorityLeagueResponse;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PrimaryLeagueProjection;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateTournamentRequest request, @MappingTarget TournamentEntity entity);

    @Mapping(target = "players", ignore = true)
    AdminTournamentResponse toAdminResponse(TournamentEntity tournamentEntity);

    PriorityLeagueResponse toPriorityLeague(PrimaryLeagueProjection primaryLeagueProjection);
}