package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.lineup.client.MastersApiClient;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.tournament.persistence.repository.TournamentResultRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentSearchService {

    private final MastersApiClient mastersApiClient;



    public List<TournamentDto> findByDateAndPlayer(String date, String playerName) {
        final String searchName = playerName.toLowerCase();
        return mastersApiClient.loadTournaments(date).stream()
                .filter(t -> t.getPlayers() != null && t.getPlayers().stream()
                        .anyMatch(p -> p.toLowerCase().contains(searchName)))
                .toList();
    }



    public List<TournamentDto> findByDateRangeAndPlayer(String startDate, String endDate, String playerName) {
        final String searchName = playerName.toLowerCase();
        List<TournamentDto> allTournaments = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        LocalDate current = start;
        while (!current.isAfter(end)) {
            String dateStr = current.toString();
            try {
                List<TournamentDto> dayTournaments = mastersApiClient.loadTournaments(dateStr);
                List<TournamentDto> filtered = dayTournaments.stream()
                        .filter(t -> t.getPlayers() != null && t.getPlayers().stream()
                                .anyMatch(p -> p.toLowerCase().contains(searchName)))
                        .toList();
                allTournaments.addAll(filtered);
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Failed to load tournaments for {}: {}", dateStr, e.getMessage());
            }
            current = current.plusDays(1);
        }

        log.info("Tournaments found for period {}-{}: {}", startDate, endDate, allTournaments.size());
        return allTournaments;
    }
}