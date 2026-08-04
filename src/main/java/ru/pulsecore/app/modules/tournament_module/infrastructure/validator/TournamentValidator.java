package ru.pulsecore.app.modules.tournament_module.infrastructure.validator;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.dto.TournamentDto;

@Component
public class TournamentValidator {

    public boolean isValid(TournamentDto t) {
        return t.getPlayers() != null && !t.getPlayers().isEmpty();
    }
}