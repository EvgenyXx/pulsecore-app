package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.lineup.repository.LineupRepository;
import ru.pulsecore.app.modules.tournament.api.dto.TournamentLiveDto;
import ru.pulsecore.app.modules.tournament.mapper.LineupLiveMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveService {

    private final LineupRepository lineupRepository;
    private final LineupLiveMapper mapper;

    private static final int TOURNAMENT_MAX_DURATION_HOURS = 7;

    public List<TournamentLiveDto> getLive() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        return lineupRepository.findByDate(today)
                .stream()
                .filter(l -> {
                    LocalTime startTime = LocalTime.parse(l.getTime());
                    return !startTime.isAfter(now) && startTime.plusHours(TOURNAMENT_MAX_DURATION_HOURS).isAfter(now);
                })
                .map(mapper::toDto)
                .toList();
    }
}