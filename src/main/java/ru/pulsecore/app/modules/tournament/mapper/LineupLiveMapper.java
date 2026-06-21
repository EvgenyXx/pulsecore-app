package ru.pulsecore.app.modules.tournament.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.modules.lineup.domain.Lineup;
import ru.pulsecore.app.modules.tournament.api.dto.TournamentLiveDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", imports = {Arrays.class, Collections.class})
public interface LineupLiveMapper {

    @Mapping(source = "id", target = "externalId")
    @Mapping(source = "league", target = "league")
    @Mapping(source = "hall", target = "hall")
    @Mapping(source = "time", target = "time")
    @Mapping(target = "date", expression = "java(lineup.getDate().toString())")
    @Mapping(target = "players", expression = "java(mapPlayers(lineup.getPlayers()))")
    @Mapping(source = "streamUrl", target = "streamUrl")
    TournamentLiveDto toDto(Lineup lineup);

    default List<String> mapPlayers(String players) {
        if (players == null || players.isBlank()) return Collections.emptyList();
        return Arrays.stream(players.split(", ")).toList();
    }
}