package ru.pulsecore.app.modules.tournament.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.lineup.repository.LineupRepository;
import ru.pulsecore.app.modules.tournament.api.dto.TournamentLiveDto;
import ru.pulsecore.app.modules.tournament.domain.LiveStatus;
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
                .map(l -> {
                    TournamentLiveDto dto = mapper.toDto(l);
                    LocalTime startTime = LocalTime.parse(l.getTime());
                    if (startTime.isAfter(now)) {
                        dto.setStatus(LiveStatus.UPCOMING);
                    } else if (startTime.plusHours(TOURNAMENT_MAX_DURATION_HOURS).isAfter(now)) {
                        dto.setStatus(LiveStatus.LIVE);
                    } else {
                        dto.setStatus(LiveStatus.FINISHED);
                    }
                    return dto;
                })
                .toList();
    }
}