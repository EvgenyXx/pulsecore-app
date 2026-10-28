package ru.pulsecore.app.tournament.application.roster;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.client.MastersApiClient;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;
import ru.pulsecore.app.tournament.infrastructure.util.NumberUtils;
import ru.pulsecore.app.tournament.infrastructure.util.PlayerIdentity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * используется в дискавери для поиска новых турниров
 * и чендж сервисе для поиска замены игроков в турнире или их удаления
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentDataProvider {

    private static final int FORECAST_DAYS = 3;
    private final MastersApiClient apiClient;

    public Map<String, List<TournamentDto>> findPlayerTournaments(List<String> names) {
        Map<String, List<TournamentDto>> all = getAllTournamentsFor3Days();

        Map<String, String> normalizedNames = names.stream()
                .collect(Collectors.toMap(PlayerIdentity::cleanName, name -> name, (n1, n2) -> n1));

        log.debug("Поиск турниров. Очищенные имена для поиска: {}", normalizedNames.keySet());

        Map<String, List<TournamentDto>> result = names.stream()
                .collect(Collectors.toMap(name -> name, name -> new ArrayList<>()));

        for (List<TournamentDto> dayTournaments : all.values()) {
            for (TournamentDto t : dayTournaments) {
                if (t.getPlayers() == null) continue;

                for (String player : t.getPlayers()) {
                    String normalized = PlayerIdentity.cleanName(player);
                    String originalName = normalizedNames.get(normalized);

                    if (originalName != null) {
                        t.setHallNumber(NumberUtils.extractInt(t.getHall()));
                        result.get(originalName).add(t);
                        log.debug("Сравнение: API='{}' (очищено='{}') -> СОВПАЛО с БД='{}'. Ссылка: {}",
                                player, normalized, originalName, t.getLink());
                    }
                }
            }
        }

        return result;
    }

    public Map<String, List<TournamentDto>> getAllTournamentsFor3Days() {
        Map<String, List<TournamentDto>> all = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < FORECAST_DAYS; i++) {
            String date = today.plusDays(i).toString();

            all.put(date, loadTournamentsForDate(date));
        }
        return all;
    }


    private List<TournamentDto> loadTournamentsForDate(String date) {
        try {
            List<TournamentDto> tournaments = apiClient.loadTournaments(date);
            return tournaments != null ? tournaments : List.of();
        } catch (Exception e) {
            log.error("Не удалось загрузить турниры на дату: {}", date, e);
            return List.of();
        }
    }
}
