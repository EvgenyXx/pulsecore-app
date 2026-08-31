package ru.pulsecore.app.tournament.application.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.admin.api.dto.request.UpdateTournamentRequest;
import ru.pulsecore.app.shared.dto.response.AdminTournamentResponse;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.exception.TournamentNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.persistence.mapper.TournamentUpdateMapper;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTournamentUpdateService {

    private final TournamentRepository tournamentRepository;
    private final TournamentUpdateMapper mapper;
    private final AdminTournamentQueryService queryService;

    @Transactional
    public AdminTournamentResponse update(Long id, UpdateTournamentRequest request) {
        log.info("Обновление турнира id={}", id);

        TournamentEntity tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException(id.toString()));

        mapper.updateEntity(request, tournament);
        tournamentRepository.save(tournament);

        return queryService.getTournamentById(id);
    }
}