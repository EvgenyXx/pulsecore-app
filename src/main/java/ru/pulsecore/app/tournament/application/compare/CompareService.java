package ru.pulsecore.app.tournament.application.compare;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.api.dto.response.PlayerCompareDto;
import ru.pulsecore.app.tournament.api.dto.response.PlayerMatchStatsDto;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentMatchRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompareService {

    private final TournamentResultRepository repository;
    private final TournamentMatchRepository matchRepository;



    public List<PlayerCompareDto> getPlayersForCompare(LocalDate start, LocalDate end) {

        LocalDate effectiveStart = start != null ? start : LocalDate.of(2000, 1, 1);
        LocalDate effectiveEnd = end != null ? end : LocalDate.now();

        return repository.findPlayersForCompare(effectiveStart, effectiveEnd)
                .stream()
                .map(PlayerCompareDto::from)
                .toList();
    }

    public List<PlayerMatchStatsDto> getPlayersMatchStats(LocalDate start, LocalDate end) {
        LocalDateTime effectiveStart = start != null
                ? start.atStartOfDay()
                : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime effectiveEnd = end != null
                ? end.plusDays(1).atStartOfDay()
                : LocalDate.now().plusDays(1).atStartOfDay();

        return matchRepository.findPlayersMatchStats(effectiveStart, effectiveEnd)
                .stream()
                .map(p -> PlayerMatchStatsDto.builder()
                        .playerName(p.getPlayerName())
                        .groupWinPercent(p.getGroupWinPercent())
                        .semifinalWinPercent(p.getSemifinalWinPercent())
                        .thirdPlaceWinPercent(p.getThirdPlaceWinPercent())
                        .finalWinPercent(p.getFinalWinPercent())
                        .build())
                .toList();
    }



}