package ru.pulsecore.app.tournament.application.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.AdminTournamentResponse;
import ru.pulsecore.app.shared.dto.response.PlayerEarning;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;
import ru.pulsecore.app.tournament.infrastructure.repository.projection.TournamentAdminProjection;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTournamentQueryService {

    private final TournamentRepository tournamentRepository;
    private final ObjectMapper objectMapper;


    @Transactional(readOnly = true)
    public List<AdminTournamentResponse> getTournamentsByDate(LocalDate date) {
        log.debug("Запрос турниров по дате: {}", date);
        return tournamentRepository.findTournamentsWithPlayersByDate(date).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminTournamentResponse getTournamentById(Long id) {
        log.debug("Запрос турнира по ID: {}", id);
        TournamentAdminProjection projection = tournamentRepository.findTournamentWithPlayersById(id);
        if (projection == null) {
            log.warn("Турнир не найден: {}", id);
            return null;
        }
        return toResponse(projection);
    }

    private AdminTournamentResponse toResponse(TournamentAdminProjection p) {
        List<PlayerEarning> players = parsePlayerEarnings(p.getPlayers());
        return new AdminTournamentResponse(
                p.getId(),
                p.getLink(),
                p.getDate(),
                p.getTime(),
                Boolean.TRUE.equals(p.getStarted()),
                Boolean.TRUE.equals(p.getFinished()),
                Boolean.TRUE.equals(p.getCancelled()),
                Boolean.TRUE.equals(p.getProcessed()),
                players
        );
    }

    private List<PlayerEarning> parsePlayerEarnings(String json) {
        if (json == null || json.isBlank()) return List.of();

        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("Не удалось распарсить players: {}", e.getMessage());
            return List.of();
        }
    }


}