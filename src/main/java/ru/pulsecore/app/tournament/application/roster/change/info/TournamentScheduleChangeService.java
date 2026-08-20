package ru.pulsecore.app.tournament.application.roster.change.info;

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
import ru.pulsecore.app.tournament.application.roster.change.TransferInfo;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.TournamentRepository;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.projection.TournamentProjection;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.NumberUtils;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentScheduleChangeService {

    private final ApplicationEventPublisher eventPublisher;
    private final TournamentRepository tournamentRepository;
    private final PlayerNotificationRepository playerNotificationRepository;

    @Transactional
    public void processScheduleChange(
            List<PlayerData> oldPlayers,
            String link,
            TournamentDto newTournament) {

        log.debug("Расписание: проверка изменений для link={}", link);

        TournamentProjection p = tournamentRepository.findDtoByLink(link).stream().findFirst().orElseThrow();
        TournamentDto old = buildOldTournament(p, newTournament);

        TransferInfo info = TransferInfo.of(old, newTournament);

        if (!info.timeChanged() && !info.dateChanged() && !info.hallChanged()) {
            log.debug("Расписание: изменений нет для link={}", link);
            return;
        }

        log.debug("Расписание: обнаружены изменения time={}, date={}, hall={}",
                info.timeChanged(), info.dateChanged(), info.hallChanged());

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
            log.debug("Расписание: обновлено время={} для tournamentId={}", newTime, tournamentId);
        }

        if (info.dateChanged()) {
            LocalDate newDate = DateTimeUtils.parseDate(newTournament.getDate().getDate());
            tournamentRepository.updateDateById(tournamentId, newDate);
            log.debug("Расписание: обновлена дата={} для tournamentId={}", newDate, tournamentId);
        }

        if (info.hallChanged()) {
            Integer newHall = NumberUtils.extractInt(newTournament.getHall());
            playerNotificationRepository.updateHallByTournamentId(tournamentId, newHall);
            log.debug("Расписание: обновлён зал={} для tournamentId={}", newHall, tournamentId);
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
        log.debug("Расписание: уведомление отправлено игроку={}", player.playerName());
    }
}