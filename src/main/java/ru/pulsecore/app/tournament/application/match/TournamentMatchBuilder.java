package ru.pulsecore.app.tournament.application.match;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentMatchEntity;
import ru.pulsecore.app.tournament.domain.model.Match;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TournamentMatchBuilder {

    private final TournamentMatchFactory matchFactory;

    public List<TournamentMatchEntity> buildMatches(ParsedResult parsed, TournamentEntity tournament) {
        if (parsed.matches() == null || parsed.matches().isEmpty()) {
            return List.of();
        }

        // Берем дату турнира (или текущую, если нет)
        LocalDateTime playedAt = parsed.date() != null
                ? DateTimeUtils.parseDate(parsed.date()).atStartOfDay()
                : LocalDateTime.now();

        return parsed.matches().stream()
                .filter(Match::isCompleted) // Только завершенные матчи
                .map(m -> matchFactory.create(tournament, m, playedAt))
                .toList();
    }
}