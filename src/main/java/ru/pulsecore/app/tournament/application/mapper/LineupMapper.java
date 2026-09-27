package ru.pulsecore.app.tournament.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.tournament.api.dto.response.LineupDto;
import ru.pulsecore.app.tournament.domain.entity.Lineup;

@Mapper(componentModel = "spring")
public interface LineupMapper {

    @Mapping(source = "date", target = "date", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "type", target = "type")
    LineupDto toDto(Lineup lineup);
}