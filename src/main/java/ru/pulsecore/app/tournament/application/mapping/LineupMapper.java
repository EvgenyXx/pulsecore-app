package ru.pulsecore.app.tournament.application.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.tournament.api.dto.response.LineupDto;
import ru.pulsecore.app.tournament.domain.entity.Lineup;

@Mapper(componentModel = "spring")
public interface LineupMapper {


    @Mapping(target = "isPlayer",ignore = true)
    LineupDto toDto(Lineup lineup);
}