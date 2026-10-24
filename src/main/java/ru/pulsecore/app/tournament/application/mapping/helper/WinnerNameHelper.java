package ru.pulsecore.app.tournament.application.mapping.helper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.util.PlayerNameUtils;

@Component
public class WinnerNameHelper {

    @Named(MappingConstants.NORMALIZE_WINNER_NAME)
    public String nameNormalize(String winnerName){
        if (winnerName==null || winnerName.isEmpty()){
            return winnerName;
        }
        return PlayerNameUtils.shortName(winnerName);
    }
}
