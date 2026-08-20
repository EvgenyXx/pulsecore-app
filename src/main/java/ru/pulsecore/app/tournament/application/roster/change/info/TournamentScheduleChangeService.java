package ru.pulsecore.app.tournament.application.roster.change;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.context.TournamentScheduleChangedContext;
import ru.pulsecore.app.shared.dto.response.DateDto;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.shared.event.MailNotificationEvent;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.TournamentProjection;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.NumberUtils;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис обработки изменений расписания турнира (зал, время, дата, лига),
 * когда состав игроков не менялся.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentScheduleChangeService {

    private final ApplicationEventPublisher eventPublisher;
    private final TournamentRepository tournamentRepository;
    private final PlayerNotificationRepository playerNotificationRepository;

    /**
     * Проверяет измениться ли расписание турнира (без учёта состава).
     * Если да — отправляет уведомление всем игрокам.
     *
     * @param oldPlayers игроки, привязанные к турниру
     * @param link старый турнир (из базы)
     * @param newTournament новый турнир (из API)
     */
    @Transactional
    public void processScheduleChange(
            List<PlayerData> oldPlayers,
            String link,
            TournamentDto newTournament) {

        TournamentProjection p = tournamentRepository.findDtoByLink(link).stream().findFirst().orElseThrow();
        TournamentDto old = buildOldTournament(p, newTournament);

        TransferInfo info = TransferInfo.of(old, newTournament);

        if (!info.timeChanged() && !info.dateChanged() && !info.hallChanged()) {
            return;
        }

        updateSchedule(p.getId(), info, newTournament);
        notifyPlayers(oldPlayers, info, newTournament);
    }

    private TournamentDto buildOldTournament(TournamentProjection p, TournamentDto newTournament) {
        TournamentDto old = new TournamentDto();
        old.setId(p.getExternalId());
        old.setLink(p.getLink());
        old.setPlayers(newTournament.getPlayers());

        DateDto dateDto = new DateDto();
        dateDto.setDate(p.getDate() + " " + p.getTime() + ":00.000000");
        old.setDate(dateDto);

        old.setHall(p.getHall() != null ? "№" + p.getHall() : null);
        old.setLeague(newTournament.getLeague());
        return old;
    }

    private void updateSchedule(Long tournamentId, TransferInfo info, TournamentDto newTournament) {
        if (info.timeChanged()) {
            String newTime = DateTimeUtils.parseTime(newTournament.getDate().getDate());
            tournamentRepository.updateTimeById(tournamentId, newTime);
        }

        if (info.dateChanged()) {
            LocalDate newDate = DateTimeUtils.parseDate(newTournament.getDate().getDate());
            tournamentRepository.updateDateById(tournamentId, newDate);
        }

        if (info.hallChanged()) {
            Integer newHall = NumberUtils.extractInt(newTournament.getHall());
            playerNotificationRepository.updateHallByTournamentId(tournamentId, newHall);
        }
    }

    private void notifyPlayers(List<PlayerData> oldPlayers, TransferInfo info, TournamentDto newTournament) {
        log.info("📅 Изменение расписания в турнире {} ({}): уведомлено игроков: {}",
                newTournament.getTitle(), newTournament.getId(), oldPlayers.size());

        oldPlayers.forEach(player -> sendScheduleChangeNotification(player, info));
    }

    private void sendScheduleChangeNotification(PlayerData player, TransferInfo info) {
        eventPublisher.publishEvent(
                new MailNotificationEvent(
                        MailTypes.TOURNAMENT_SCHEDULE_CHANGED,
                        new TournamentScheduleChangedContext(
                                player.email(),
                                StringUtils.extractFirstName(player.playerName()),
                                info
                        )
                )
        );
    }
}