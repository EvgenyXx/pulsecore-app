package ru.pulsecore.app.tournament.infrastructure.parser.league;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.enums.LeagueType;
import java.util.Comparator;
import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
public class LeagueDetector {

     /**
     * Spring автоматически соберёт сюда ВСЕ бины, реализующие LeagueResolver.
     * Сейчас это JsonLeagueResolver (order=10) и TextLeagueResolver (order=100).
     */
    private final List<LeagueResolver> resolvers;

    public LeagueType detectLeague(TournamentPage page) {
        if (page == null) return null;

        List<LeagueResolver> ordered = resolvers.stream()
                .sorted(Comparator.comparingInt(LeagueResolver::order))
                .toList();

        LeagueResolver primary = ordered.isEmpty() ? null : ordered.get(0);

        for (LeagueResolver resolver : ordered) {
            try {
                LeagueType result = resolver.resolve(page);
                if (result == null) continue;

                if (primary != null && resolver != primary) {
                    log.warn("League detected by fallback resolver {}: {}",
                            resolver.getClass().getSimpleName(), result);
                }
                return result;
            } catch (Exception e) {
                log.warn("LeagueResolver {} failed: {}",
                        resolver.getClass().getSimpleName(), e.getMessage());
            }
        }

        log.warn("Could not detect league from any resolver");
        return null;
    }



}