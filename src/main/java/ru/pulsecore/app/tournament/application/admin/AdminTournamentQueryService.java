package ru.pulsecore.app.tournament.application.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.AdminTournamentResponse;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.TournamentAdminProjection;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTournamentQueryService {

    private final TournamentRepository tournamentRepository;

    @Transactional(readOnly = true)
    public List<AdminTournamentResponse> getTournamentsByDate(LocalDate date) {
        log.info("Запрос турниров по дате: {}", date);
        return tournamentRepository.findTournamentsWithPlayersByDate(date).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminTournamentResponse getTournamentById(Long id) {
        log.info("Запрос турнира по ID: {}", id);
        TournamentAdminProjection projection = tournamentRepository.findTournamentWithPlayersById(id);
        if (projection == null) {
            log.warn("Турнир не найден: {}", id);
            return null;
        }
        return toResponse(projection);
    }

    private AdminTournamentResponse toResponse(TournamentAdminProjection p) {
        List<String> players = parsePlayers(p.getPlayers());
        return new AdminTournamentResponse(
                p.getId(),
                p.getLink(),
                p.getDate(),
                p.getTime(),
                p.getStarted(),
                p.getFinished(),
                p.getCancelled(),
                p.getProcessed(),
                players
        );
    }

    private List<String> parsePlayers(String playersRaw) {
        if (playersRaw == null || playersRaw.isBlank() || playersRaw.equals("{}")) {
            return List.of();
        }
        return Arrays.stream(playersRaw.replace("{", "").replace("}", "").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}