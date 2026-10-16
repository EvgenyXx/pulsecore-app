package ru.pulsecore.app.tournament.application.mapping;

import org.mapstruct.Mapper;
import ru.pulsecore.app.tournament.api.dto.response.PlayerH2HMatchDto;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PlayerH2HMatchProjection;

@Mapper(componentModel = "spring")
public interface TournamentMatchMapper {

    PlayerH2HMatchDto toH2HMatchDto(PlayerH2HMatchProjection projection);
}