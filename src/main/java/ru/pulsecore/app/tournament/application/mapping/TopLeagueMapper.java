package ru.pulsecore.app.tournament.application.mapping;

import org.mapstruct.Mapper;
import ru.pulsecore.app.player.api.dto.response.TopLeagueResponse;
import ru.pulsecore.app.player.api.dto.response.TopPlayerDto;
import ru.pulsecore.app.tournament.application.mapping.helper.TopPlayersViewMapperHelper;
import ru.pulsecore.app.tournament.domain.entity.TopPlayersView;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TopLeagueMapper {

    TopPlayerDto toDto(TopPlayersView view);
    List<TopPlayerDto> toDtoList(List<TopPlayersView> views);

    default TopLeagueResponse toLeagueResponse(List<TopPlayersView> players, UUID playerId) {
        return TopLeagueResponse.builder()
                .top5(toDtoList(players))
                .playerPosition(TopPlayersViewMapperHelper.findPlayerPosition(players, playerId))
                .playerTournaments(TopPlayersViewMapperHelper.findPlayerTournaments(players, playerId))
                .build();
    }

    default TopLeagueResponse toLeagueResponseWithoutPlayer(List<TopPlayersView> players) {
        return TopLeagueResponse.builder()
                .top5(toDtoList(players))
                .playerPosition(0)
                .playerTournaments(0)
                .build();
    }
}