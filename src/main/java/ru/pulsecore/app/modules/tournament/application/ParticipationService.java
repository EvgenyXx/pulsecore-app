package ru.pulsecore.app.modules.tournament.application;

import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.tournament.domain.ParsedResult;

@Service
public class ParticipationService {

    public boolean isUserInParsed(ParsedResult parsed, String playerName) {
        if (parsed == null || parsed.results() == null) return false;

        return parsed.results().stream()
                .anyMatch(r ->
                        r.getPlayer() != null &&
                                r.getPlayer().equalsIgnoreCase(playerName)
                );
    }
}