package ru.pulsecore.app.tournament.application.compare;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentResultRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.PlayerCompareResponse;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompareService {

    private final TournamentResultRepository repository;

    public List<PlayerCompareResponse> getPlayersForCompare(LocalDate start, LocalDate end) {
        LocalDate effectiveStart = start != null ? start : LocalDate.of(2000, 1, 1);
        LocalDate effectiveEnd = end != null ? end : LocalDate.now();
        return repository.findPlayersForCompare(effectiveStart, effectiveEnd);
    }
}