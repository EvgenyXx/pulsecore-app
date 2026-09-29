package ru.pulsecore.app.tournament.application.mapping;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.pulsecore.app.admin.api.dto.request.UpdateTournamentRequest;
import ru.pulsecore.app.shared.dto.response.PriorityLeagueResponse;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PrimaryLeagueProjection;

@Mapper(componentModel = "spring")
public interface TournamentMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateTournamentRequest request, @MappingTarget TournamentEntity entity);

    PriorityLeagueResponse toPriorityLeague(PrimaryLeagueProjection primaryLeagueProjection);
}