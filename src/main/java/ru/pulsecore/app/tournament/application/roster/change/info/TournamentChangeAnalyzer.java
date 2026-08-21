package ru.pulsecore.app.tournament.application.roster.change.info;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.change.remove.PlayerReplacementService;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.exception.TournamentNotFoundException;

import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentChangeAnalyzer {

    private final TournamentRepository tournamentRepository;
    private final PlayerNotificationRepository notificationRepository;
    private final PlayerClient playerClient;
    private final PlayerReplacementService replacementService;
    private final TournamentScheduleChangeService scheduleChangeService;

    public void analyze(TournamentDto newTournament, Map<String, List<TournamentDto>> allTournaments) {
        log.debug("Анализ: турнир={}, link={}", newTournament.getId(), newTournament.getLink());

        TournamentEntity oldTournament = tournamentRepository
                .findByLink(newTournament.getLink())
                .orElseThrow(()-> new TournamentNotFoundException(newTournament.getLink()));

        Set<UUID> oldPlayerIds = notificationRepository
                .findPlayerIdsByTournamentId(oldTournament.getId());

        log.debug("Анализ: старый турнир id={}, игроков={}",
                oldTournament.getId(), oldPlayerIds.size());

        List<PlayerData> oldPlayers = playerClient.getPlayerDataByIds(oldPlayerIds);

        boolean playersChanged = replacementService.processReplacement(
                oldPlayers, newTournament, oldTournament.getId(), allTournaments);

        if (!playersChanged) {
            log.debug("Анализ: состав не изменился, проверяем расписание");
            scheduleChangeService.processScheduleChange(oldPlayers, oldTournament.getLink(), newTournament);
        }

        log.debug("Анализ: завершён для турнира={}", newTournament.getId());
    }
}