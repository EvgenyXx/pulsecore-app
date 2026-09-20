package ru.pulsecore.app.tournament.application.roster.change.info;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.change.remove.PlayerReplacementService;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.util.*;

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

        Optional<TournamentEntity> oldTournamentOpt = tournamentRepository
                .findByLink(newTournament.getLink());

        if (oldTournamentOpt.isEmpty()) {
            log.warn("Турнир {} ещё не сохранён в БД — пропускаем проверку", newTournament.getLink());
            return;
        }

        TournamentEntity oldTournament = oldTournamentOpt.get();

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