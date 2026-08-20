package ru.pulsecore.app.tournament.application.roster.finish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.tournament.application.resolution.ResultService;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.domain.model.ParsedResult;
import ru.pulsecore.app.tournament.domain.enums.TournamentStatus;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentFinishService {


    private final ResultService resultService;
    private final TournamentProcessService processService;
    private final PlayerNotificationRepository repo;
    private final TournamentRepository tournamentRepository;

    @Transactional
    public void handleFinished(TournamentEntity t,
                               List<PlayerNotification> notifications,
                               Document doc)  {

        ParsedResult parsed = resultService.calculateAll(doc);

        if (parsed == null) {
            log.warn("Турнир {} пропущен: невозможно проанализировать", t.getLink());
            return;
        }
        if (parsed.league() == null) {
            return;
        }
        if (parsed.status() != TournamentStatus.FINISHED) return;
        processService.processTournament(notifications, parsed);
        t.setFinished(true);
        t.setProcessed(true);
        tournamentRepository.save(t);
        repo.saveAll(notifications);
        log.info("Турнир завершился. Дата:{},Время:{},URL:{}",t.getDate(),t.getTime(),doc.baseUri());

    }
}