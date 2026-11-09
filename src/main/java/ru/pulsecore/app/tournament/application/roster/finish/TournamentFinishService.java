package ru.pulsecore.app.tournament.application.roster.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.api.player.PlayerTournamentClient;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.application.match.TournamentMatchService;
import ru.pulsecore.app.tournament.application.resolution.ResultService;
import ru.pulsecore.app.tournament.domain.TournamentPage;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.domain.entity.TournamentResultEntity;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.infrastructure.exception.TournamentNotFoundException;
import ru.pulsecore.app.tournament.infrastructure.repository.TournamentRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentFinishService {

    private final ResultService resultService;
    private final TournamentRepository tournamentRepository;
    private final TournamentResultNotifier tournamentResultNotifier;
    private final TournamentResultResolver resultProcessor;
    private final PlayerTournamentClient playerClient;
    private final TournamentMatchService matchService;
    private final TournamentResultPersistence tournamentResultPersistence;

    @Transactional
    public void handleFinished(String link,
                               TournamentPage page) {

        ParsedResult parsed = resultService.calculateAll(page);

        if (parsed.status() != TournamentStatus.FINISHED) return;

        TournamentEntity tournament = tournamentRepository.findByLink(link)
                .orElseThrow(() -> new TournamentNotFoundException(link));

        Set<UUID> playerIds = tournament.getNotifications()
                .stream().map(PlayerNotification::getPlayerId).collect(Collectors.toSet());

        List<PlayerData> roster = playerClient.getPlayerDataByIds(playerIds);

        Map<UUID, String> rosterData = roster.stream()
                .collect(Collectors.toMap(
                        PlayerData::id,
                        PlayerData::name
                ));

        List<TournamentResultEntity> resultEntities = resultProcessor.processResultsRoster(
                parsed.results(), rosterData, tournament,
                parsed.nightBonus(),
                parsed.hasRemoved(),
                parsed.league());

        tournamentResultPersistence.saveResults(resultEntities);
        matchService.createMatches(parsed, tournament);

        tournament.setFinished(true);
        tournament.setProcessed(true);

        tournamentRepository.save(tournament);
        tournamentResultNotifier.publishLastResultEvent(resultEntities);

        log.info("Турнир завершился. Дата:{}, Время:{}, URL:{}",
                tournament.getDate(), tournament.getTime(),
                page.document() != null ? page.document().baseUri() : "null");
    }
}