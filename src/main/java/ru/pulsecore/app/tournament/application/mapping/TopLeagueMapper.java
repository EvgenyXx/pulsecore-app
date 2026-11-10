package ru.pulsecore.app.tournament.application.mapping;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.player.api.dto.response.DetailPeriod;
import ru.pulsecore.app.player.api.dto.response.TopLeagueResponse;
import ru.pulsecore.app.player.api.dto.response.TopPlayerDto;
import ru.pulsecore.app.tournament.application.mapping.helper.TopPlayerMapperHelper;
import ru.pulsecore.app.tournament.application.mapping.helper.TopPlayersViewMapperHelper;
import ru.pulsecore.app.tournament.domain.entity.TopPlayersView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        imports = TopPlayerMapperHelper.class
)
public interface TopLeagueMapper {

    @Mapping(target = "detailPeriod", expression = "java(details.get(view.getPlayerId()))")
    @Mapping(target = "winrate", expression = "java(TopPlayerMapperHelper.calculateWinrate(details.get(view.getPlayerId())))")
    TopPlayerDto toDto(TopPlayersView view, @Context Map<UUID, DetailPeriod> details);

    List<TopPlayerDto> toDtoList(List<TopPlayersView> views, @Context Map<UUID, DetailPeriod> details);

    default TopLeagueResponse toLeagueResponse(List<TopPlayersView> players, UUID playerId, @Context Map<UUID, DetailPeriod> details) {
        return TopLeagueResponse.builder()
                .top5(toDtoList(players, details))
                .playerPosition(TopPlayersViewMapperHelper.findPlayerPosition(players, playerId))
                .playerTournaments(TopPlayersViewMapperHelper.findPlayerTournaments(players, playerId))
                .build();
    }

    default TopLeagueResponse toLeagueResponseWithoutPlayer(List<TopPlayersView> players, @Context Map<UUID, DetailPeriod> details) {
        return TopLeagueResponse.builder()
                .top5(toDtoList(players, details))
                .playerPosition(0)
                .playerTournaments(0)
                .build();
    }
}