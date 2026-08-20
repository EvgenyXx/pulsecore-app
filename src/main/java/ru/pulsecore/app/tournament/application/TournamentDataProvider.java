package ru.pulsecore.app.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.infrastructure.client.MastersApiClient;
import ru.pulsecore.app.tournament.infrastructure.util.NameNormalizer;
import ru.pulsecore.app.tournament.infrastructure.util.NumberUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentDataProvider {

    private static final int FORECAST_DAYS = 3;
    private final MastersApiClient apiClient;

    public Map<String, List<TournamentDto>> findPlayerTournaments(List<String> names) {

        Map<String, List<TournamentDto>> all = getAllTournamentsFor3Days();


        Map<String, String> normalizedNames = names.stream()
                .collect(Collectors.toMap(NameNormalizer::normalizeForSearch, name -> name));

        Map<String, List<TournamentDto>> result = names.stream()
                .collect(Collectors.toMap(name -> name, name -> new ArrayList<>()));

        for (List<TournamentDto> dayTournaments : all.values()) {
            for (TournamentDto t : dayTournaments) {
                if (t.getPlayers() == null) continue;

                for (String player : t.getPlayers()) {
                    String normalized = NameNormalizer.normalizeForSearch(player);
                    String originalName = normalizedNames.get(normalized);

                    if (originalName != null) {
                        t.setHallNumber(NumberUtils.extractInt(t.getHall()));
                        result.get(originalName).add(t);

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
            log.error("Failed to load tournaments for date: {}", date, e);
            return List.of();
        }
    }
}
