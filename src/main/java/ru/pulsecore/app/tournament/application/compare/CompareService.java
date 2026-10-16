package ru.pulsecore.app.tournament.application.compare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.tournament.api.dto.response.*;
import ru.pulsecore.app.tournament.application.mapping.TournamentMatchMapper;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentMatchRepository;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentResultRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompareService {


    private final TournamentMatchRepository matchRepository;
    private final TournamentMatchMapper matchMapper;

    public PlayerH2HResponseDto getH2H(String player1Name, String player2Name, LocalDate start, LocalDate end) {
        LocalDateTime effectiveStart = start != null
                ? start.atStartOfDay()
                : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime effectiveEnd = end != null
                ? end.plusDays(1).atStartOfDay()
                : LocalDate.now().plusDays(1).atStartOfDay();

        PlayerH2HSummaryDto summary = PlayerH2HSummaryDto.from(
                matchRepository.findH2HSummary(player1Name, player2Name, effectiveStart, effectiveEnd)
        );

        return PlayerH2HResponseDto.builder()
                .player1Name(player1Name)
                .player2Name(player2Name)
                .summary(summary)
                .build();
    }

    public Page<PlayerH2HMatchDto> getH2HMatches(
            String player1Name, String player2Name,
            LocalDate start, LocalDate end,
            int page, int size) {

        LocalDateTime effectiveStart = start != null
                ? start.atStartOfDay()
                : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime effectiveEnd = end != null
                ? end.plusDays(1).atStartOfDay()
                : LocalDate.now().plusDays(1).atStartOfDay();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "playedAt"));

        return matchRepository
                .findH2HMatches(player1Name, player2Name, effectiveStart, effectiveEnd, pageable)
                .map(matchMapper::toH2HMatchDto);
    }

    public Page<String> searchPlayersPage(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return matchRepository.searchPlayerNames(query, pageable);
    }
}