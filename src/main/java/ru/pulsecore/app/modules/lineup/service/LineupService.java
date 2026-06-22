package ru.pulsecore.app.modules.lineup.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.lineup.client.MastersApiClient;
import ru.pulsecore.app.modules.lineup.domain.Lineup;
import ru.pulsecore.app.modules.lineup.mapper.LineupMapper;
import ru.pulsecore.app.modules.lineup.repository.HallStreamRepository;
import ru.pulsecore.app.modules.lineup.repository.LineupRepository;
import ru.pulsecore.app.modules.lineup.validator.TournamentValidator;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LineupService {

    private final LineupRepository lineupRepository;
    private final MastersApiClient apiClient;
    private final LineupMapper mapper;
    private final TournamentValidator validator;
    private final HallStreamRepository hallStreamRepository;


    @Transactional
    public void loadTodayOnly() {
        loadDay(LocalDate.now());
        log.info("Today lineups loaded");
    }

    @Transactional
    public void cleanupOld() {
        lineupRepository.deleteByDateBefore(LocalDate.now());
        log.info("Cleaned old lineups");
    }

    @Transactional
    public void loadLineups() {
        LocalDate today = LocalDate.now();
        loadDay(today);
        loadDay(today.plusDays(1));
        loadDay(today.plusDays(2));
    }

    @Transactional
    public void loadTomorrowOnly() {
        loadDay(LocalDate.now().plusDays(1));
        log.info("Tomorrow lineups loaded");
    }

    @Transactional
    public void loadDayAfterTomorrow() {
        loadDay(LocalDate.now().plusDays(2));
        log.info("Day-after-tomorrow lineups loaded");
    }

    private void loadDay(LocalDate date) {
        List<TournamentDto> all = apiClient.loadTournaments(date.toString());
        if (all == null || all.isEmpty()) return;

        List<TournamentDto> valid = all.stream()
                .filter(validator::isValid)
                .filter(t -> date.equals(extractDate(t)))
                .toList();

        if (valid.isEmpty()) return;

        if (date.isAfter(LocalDate.now())) {
            lineupRepository.deleteAllByDate(date);
        }

        List<Lineup> lineups = valid.stream()
                .map(t -> mapper.toEntity(t, date, extractTime(t)))
                .toList();

        for (Lineup lineup : lineups) {
            if (lineup.getStreamUrl() == null || lineup.getStreamUrl().isBlank()) {
                try {
                    String url = hallStreamRepository.findStreamUrlByHall(lineup.getHall());
                    if (url != null) {
                        lineup.setStreamUrl(url);
                    }
                } catch (Exception e) {
                    log.warn("No stream URL for hall {}", lineup.getHall());
                }
            }
            lineupRepository.upsertLineup(
                    lineup.getDate(),
                    lineup.getLeague(),
                    lineup.getTime(),
                    lineup.getHall(),
                    lineup.getPlayers(),
                    lineup.getStreamUrl()
            );
        }

        log.info("{} lineups saved for date {}", lineups.size(), date);
    }

    public List<Lineup> getLineupsForHalls(LocalDate date, List<String> halls) {
        if (halls == null || halls.isEmpty()) return List.of();
        return lineupRepository.findByDateAndHallIn(date, halls);
    }

    public Map<String, List<Lineup>> groupByHall(List<Lineup> lineups) {
        return lineups.stream()
                .collect(Collectors.groupingBy(l -> l.getHall() != null ? l.getHall() : "Без зала",
                        LinkedHashMap::new, Collectors.toList()));
    }

    private LocalDate extractDate(TournamentDto t) {
        try {
            return LocalDate.parse(t.getDate().getDate().substring(0, 10));
        } catch (Exception e) {
            return null;
        }
    }

    private String extractTime(TournamentDto t) {
        try {
            return t.getDate().getDate().substring(11, 16);
        } catch (Exception e) {
            return "??:??";
        }
    }
}