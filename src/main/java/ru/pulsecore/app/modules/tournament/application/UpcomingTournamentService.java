package ru.pulsecore.app.modules.tournament.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.lineup.client.MastersApiClient;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpcomingTournamentService {

    private final MastersApiClient apiClient;

    // Один запрос к Masters на всех игроков, кэш на 15 минут
    @Cacheable(value = "allTournaments", key = "'3days'")
    public Map<String, List<TournamentDto>> getAllTournamentsFor3Days() {
        Map<String, List<TournamentDto>> all = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 3; i++) {
            String date = today.plusDays(i).toString();
            try {
                List<TournamentDto> tournaments = apiClient.loadTournaments(date);
                all.put(date, tournaments != null ? tournaments : List.of());
            } catch (Exception e) {
                log.error("Failed to load tournaments for date: {}", date, e);
                all.put(date, List.of());
            }
        }
        return all;
    }

    // Быстрый поиск по кэшу — без запросов к Masters
    public List<TournamentDto> findPlayerTournaments(String searchName) {
        Map<String, List<TournamentDto>> all = getAllTournamentsFor3Days();
        String normalized = normalize(searchName);
        List<TournamentDto> result = new ArrayList<>();

        for (List<TournamentDto> dayTournaments : all.values()) {
            for (TournamentDto t : dayTournaments) {
                if (t.getPlayers() == null) continue;
                for (String player : t.getPlayers()) {
                    if (player != null && normalize(player).equals(normalized)) {
                        t.setHallNumber(extractHallNumber(t.getHall()));
                        result.add(t);
                        break;
                    }
                }
            }
        }
        return result;
    }

    private Integer extractHallNumber(String hall) {
        if (hall == null) return null;
        try {
            return Integer.parseInt(hall.replaceAll("\\D+", ""));
        } catch (Exception e) {
            return null;
        }
    }

    private String normalize(String name) {
        if (name == null) return "";
        return name.toLowerCase()
                .replace("\u00A0", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}