package ru.pulsecore.app.tournament.infrastructure.validator;

import org.springframework.stereotype.Component;
import ru.pulsecore.app.shared.dto.TournamentDto;

@Component
public class TournamentValidator {

    public boolean isValid(TournamentDto t) {
        return t.getPlayers() != null && !t.getPlayers().isEmpty();
    }
}