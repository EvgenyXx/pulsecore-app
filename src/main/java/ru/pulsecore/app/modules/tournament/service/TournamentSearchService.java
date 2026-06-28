package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.lineup.client.MastersApiClient;
import ru.pulsecore.app.modules.shared.service.NameNormalizer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentSearchService {

    private static final long API_DELAY_MS = 500;

    private final MastersApiClient mastersApiClient;
    private final NameNormalizer nameNormalizer;

    public List<TournamentDto> findByDateAndPlayer(String date, String playerName) {
        String searchName = nameNormalizer.normalizeForSearch(playerName);
        return filterByPlayer(mastersApiClient.loadTournaments(date), searchName);
    }

    public List<TournamentDto> findByDateRangeAndPlayer(String startDate, String endDate, String playerName) {
        String searchName = nameNormalizer.normalizeForSearch(playerName);
        List<TournamentDto> allTournaments = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        for (LocalDate current = start; !current.isAfter(end); current = current.plusDays(1)) {
            List<TournamentDto> dayTournaments = loadDayTournaments(current.toString());
            allTournaments.addAll(filterByPlayer(dayTournaments, searchName));
            sleepBetweenRequests();
        }

        log.info("Tournaments found for period {}-{}: {}", startDate, endDate, allTournaments.size());
        return allTournaments;
    }

    private List<TournamentDto> loadDayTournaments(String date) {
        try {
            return mastersApiClient.loadTournaments(date);
        } catch (Exception e) {
            log.error("Failed to load tournaments for {}: {}", date, e.getMessage());
            return List.of();
        }
    }

    private List<TournamentDto> filterByPlayer(List<TournamentDto> tournaments, String searchName) {
        return tournaments.stream()
                .filter(t -> t.getPlayers() != null && t.getPlayers().stream()
                        .anyMatch(p -> nameNormalizer.normalizeForSearch(p).contains(searchName)))
                .toList();
    }

    private void sleepBetweenRequests() {
        try {
            TimeUnit.MILLISECONDS.sleep(API_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}