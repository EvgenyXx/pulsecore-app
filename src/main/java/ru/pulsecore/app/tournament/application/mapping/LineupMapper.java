package ru.pulsecore.app.tournament.application.mapping;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.tournament.api.dto.response.LineupDto;
import ru.pulsecore.app.tournament.application.mapping.helper.LineupMapperHelper;
import ru.pulsecore.app.tournament.application.mapping.helper.MappingConstants;
import ru.pulsecore.app.tournament.domain.entity.Lineup;

@Mapper(componentModel = "spring", uses = {LineupMapperHelper.class})
public interface LineupMapper {


    @Mapping(target = "isPlayer", source = "players", qualifiedByName = MappingConstants.IS_PLAYER_IN_LINEUP)
    LineupDto toDto(Lineup lineup, @Context String playerName);
}