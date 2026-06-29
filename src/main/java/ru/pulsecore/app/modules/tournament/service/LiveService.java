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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LiveService {

    private final LineupRepository lineupRepository;
    private final LineupLiveMapper mapper;
    private final ChatWebSocketService  chatWebSocketService;

    private static final int TOURNAMENT_MAX_DURATION_HOURS = 6;

    public List<TournamentLiveDto> getLive() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        return lineupRepository.findByDate(today)
                .stream()
                .map(l -> {
                    TournamentLiveDto dto = mapper.toDto(l);
                    LocalTime startTime = LocalTime.parse(l.getTime());
                    dto.setStatus(calculateStatus(startTime, now));
                    return dto;
                })
                .toList();
    }

    public Map<Long, Long> getOnlineCounts() {
        return chatWebSocketService.getAllOnlineCounts();
    }



    private LiveStatus calculateStatus(LocalTime startTime, LocalTime now) {
        if (startTime.isAfter(now)) {
            return LiveStatus.UPCOMING;
        }

        LocalTime endTime = startTime.plusHours(TOURNAMENT_MAX_DURATION_HOURS);

        if (endTime.isBefore(startTime) || endTime.isAfter(now)) {
            return LiveStatus.LIVE;
        }

        return LiveStatus.FINISHED;
    }
}