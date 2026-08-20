package ru.pulsecore.app.tournament.application.lineup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.LineupRepository;
import ru.pulsecore.app.tournament.api.dto.response.TournamentLiveDto;
import ru.pulsecore.app.tournament.domain.enums.LiveStatus;
import ru.pulsecore.app.tournament.infrastructure.persistence.mapper.LineupLiveMapper;
import ru.pulsecore.app.tournament.application.chat.ChatWebSocketService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;


/**
 * Сервис лайв-трансляций.
 * Показывает текущие матчи на сегодня со статусами (UPCOMING, LIVE, FINISHED),
 */
@Service
@RequiredArgsConstructor
public class LiveTournamentService {

    private final LineupRepository lineupRepository;
    private final LineupLiveMapper mapper;
    private final ChatWebSocketService chatWebSocketService;


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