package ru.pulsecore.app.admin.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.admin.api.dto.request.UpdateTournamentRequest;
import ru.pulsecore.app.admin.client.TournamentClient;
import ru.pulsecore.app.shared.dto.response.AdminTournamentResponse;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentAdminService {

    private final TournamentClient tournamentClient;

    public List<AdminTournamentResponse> getByDate(LocalDate date) {
        log.info("Получение турниров по дате: {}", date);
        return tournamentClient.getTournamentsByDate(date);
    }

    public AdminTournamentResponse getById(Long id) {
        log.info("Получение турнира по ID: {}", id);
        return tournamentClient.getTournamentById(id);
    }

    public AdminTournamentResponse update(Long id, UpdateTournamentRequest request) {
        log.info("Обновление турнира: {}, данные: {}", id, request);
        return tournamentClient.updateTournament(id, request);
    }
}