package ru.pulsecore.app.tournament.application.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.AdminTournamentResponse;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.application.admin.assembler.AdminTournamentAssembler;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.shared.api.player.PlayerTournamentClient;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentResultRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTournamentQueryService {

    private final TournamentRepository tournamentRepository;
    private final TournamentResultRepository tournamentResultRepository;
    private final AdminTournamentAssembler assembler;
    private final PlayerTournamentClient playerClient;



    @Transactional(readOnly = true)
    public List<AdminTournamentResponse> getTournamentsByDate(LocalDate date) {
        log.debug("Запрос турниров по дате: {}", date);

        List<TournamentEntity> tournaments = tournamentRepository.findByDateOrderByTimeAsc(date);
        if (tournaments.isEmpty()) return List.of();

        List<Long> tIds = tournaments.stream().map(TournamentEntity::getId).toList();
        List<TournamentResultEntity> results = tournamentResultRepository.findByTournamentIds(tIds);

        Set<UUID> playerIds = tournaments.stream()
                .flatMap(t -> t.getNotifications().stream())
                .map(PlayerNotification::getPlayerId)
                .collect(Collectors.toSet());

        List<PlayerData> players = playerClient.getPlayerDataByIds(playerIds);

        return assembler.assemble(tournaments, players, results);
    }




}