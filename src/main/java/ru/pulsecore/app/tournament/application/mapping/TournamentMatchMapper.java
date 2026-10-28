package ru.pulsecore.app.tournament.application.mapping;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pulsecore.app.tournament.api.dto.response.PlayerH2HMatchDto;
import ru.pulsecore.app.tournament.application.mapping.helper.MappingConstants;
import ru.pulsecore.app.tournament.application.mapping.helper.ScoreHelper;
import ru.pulsecore.app.tournament.application.mapping.helper.WinnerNameHelper;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.PlayerH2HMatchProjection;


@Mapper(
        componentModel = "spring",
        uses = {ScoreHelper.class, WinnerNameHelper.class
        }
)
public interface TournamentMatchMapper {

    @Mapping(target = "winnerName", source = "winnerName", qualifiedByName = MappingConstants.NORMALIZE_WINNER_NAME)
    @Mapping(target = "score", source = "score", qualifiedByName = MappingConstants.NORMALIZE_SCORE)
    PlayerH2HMatchDto toH2HMatchDto(PlayerH2HMatchProjection projection);
}