package ru.pulsecore.app.modules.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.lineup.client.MastersApiClient;
import ru.pulsecore.app.modules.shared.util.NumberUtils;
import ru.pulsecore.app.modules.shared.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpcomingTournamentService {

    private final MastersApiClient apiClient;

    private static final int FORECAST_DAYS = 3;

    public Map<String, List<TournamentDto>> getAllTournamentsFor3Days() {
        Map<String, List<TournamentDto>> all = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < FORECAST_DAYS; i++) {
            String date = today.plusDays(i).toString();
            all.put(date, loadTournamentsForDate(date));
        }
        return all;
    }

    public List<TournamentDto> findPlayerTournaments(String searchName) {
        Map<String, List<TournamentDto>> all = getAllTournamentsFor3Days();
        String normalized = StringUtils.normalizeSearch(searchName);
        List<TournamentDto> result = new ArrayList<>();

        for (List<TournamentDto> dayTournaments : all.values()) {
            for (TournamentDto t : dayTournaments) {
                if (containsPlayer(t, normalized)) {
                    t.setHallNumber(NumberUtils.extractInt(t.getHall()));
                    result.add(t);
                }
            }
        }
        return result;
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

    private boolean containsPlayer(TournamentDto t, String normalizedName) {
        if (t.getPlayers() == null) return false;
        for (String player : t.getPlayers()) {
            if (player != null && StringUtils.normalizeSearch(player).equals(normalizedName)) {
                return true;
            }
        }
        return false;
    }
}