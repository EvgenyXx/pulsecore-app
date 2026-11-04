package ru.pulsecore.app.tournament.application.roster.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.TournamentDataProvider;
import ru.pulsecore.app.tournament.infrastructure.util.NumberUtils;
import ru.pulsecore.app.tournament.infrastructure.util.PlayerIdentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class PlayerTournamentMatcher {

    private final TournamentDataProvider tournamentDataProvider;

    public Map<String, List<TournamentDto>> match(List<String> names) {
        List<TournamentDto> tournamentDtos = tournamentDataProvider.getTournamentsFor3days();

        Map<String, String> normalizedNames = names.stream()
                .collect(Collectors.toMap(PlayerIdentity::cleanName,
                        name -> name, (n1, n2) -> n1));

        log.debug("Поиск турниров. Очищенные имена для поиска: {}", normalizedNames.keySet());

        Map<String, List<TournamentDto>> result = names.stream()
                .collect(Collectors.toMap(name -> name, name -> new ArrayList<>()));

        for (TournamentDto t : tournamentDtos) {
            if (t.getPlayers() == null) continue;
            t.setHallNumber(NumberUtils.extractInt(t.getHall()));
            for (String player : t.getPlayers()) {
                String normalized = PlayerIdentity.cleanName(player);
                String originalName = normalizedNames.get(normalized);

                if (originalName != null) {
                    result.get(originalName).add(t);
                    log.debug("Сравнение: API='{}' (очищено='{}') -> СОВПАЛО с БД='{}'. Ссылка: {}",
                            player, normalized, originalName, t.getLink());
                }
            }
        }

        return result;
    }
}
