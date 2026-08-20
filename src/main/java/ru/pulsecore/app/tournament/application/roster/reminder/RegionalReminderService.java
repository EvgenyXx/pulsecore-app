package ru.pulsecore.app.tournament.application.roster.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Базовый класс для региональных напоминаний.
 * Содержит общую логику: загрузка уведомлений, расчёт времени,
 * проверка и отправка push-уведомлений.
 * Подклассы переопределяют: getHall(), getZone(), getRegionName().
 */
@Slf4j
@RequiredArgsConstructor
public abstract class RegionalReminderService {

    private final PlayerNotificationRepository notificationRepository;
    private final PlayerClient playerClient;
    private final ReminderNotificationSender reminderNotificationSender;

    /**
     * Зал региона. Если null — все залы.
     */
    protected abstract List<Integer> getHalls();

    /**
     * Таймзона региона.
     */
    protected abstract ZoneId getZone();

    /**
     * Название региона для логов.
     */
    protected abstract String getRegionName();

    @Transactional
    public void sendReminders() {
        List<PlayerNotification> notifications = loadNotifications();

        if (notifications.isEmpty()) {
            return;
        }

        Map<UUID, PlayerData> playerMap = loadPlayers(notifications);


        LocalDate today = LocalDate.now(getZone());
        LocalDate tomorrow = today.plusDays(1);
        LocalTime now = LocalTime.now(getZone()).withSecond(0).withNano(0);


        List<PlayerData> hourPushed = new ArrayList<>();
        List<PlayerData> eveningPushed = new ArrayList<>();

        notifications.forEach(pn -> {
            PlayerData player = playerMap.get(pn.getPlayerId());
            if (player != null) {
                processNotification(pn, today, tomorrow, now, player, hourPushed, eveningPushed);
            }
        });

        logPushes(hourPushed, eveningPushed);
    }

    private List<PlayerNotification> loadNotifications() {
        List<Integer> halls = getHalls();
        if (halls == null || halls.isEmpty()) {
            return notificationRepository.findPendingWithTournament();
        }
        return notificationRepository.findPendingByHalls(halls);
    }

    private Map<UUID, PlayerData> loadPlayers(List<PlayerNotification> notifications) {
        Set<UUID> playerIds = notifications.stream()
                .map(PlayerNotification::getPlayerId)
                .collect(Collectors.toSet());

        return playerClient.getPlayerDataByIds(playerIds).stream()
                .collect(Collectors.toMap(PlayerData::playerId, p -> p));
    }

    private void logPushes(List<PlayerData> hours, List<PlayerData> evenings) {
        if (!hours.isEmpty()) {
            log.info("{} — пуш за час отправлен: {}",
                    getRegionName(),
                    hours.stream().map(PlayerData::playerName).collect(Collectors.joining(", ")));
        }
        if (!evenings.isEmpty()) {
            log.info("{} — вечерний пуш отправлен: {}",
                    getRegionName(),
                    evenings.stream().map(PlayerData::playerName).collect(Collectors.joining(", ")));
        }
    }

    private void processNotification(
            PlayerNotification pn,
            LocalDate today,
            LocalDate tomorrow,
            LocalTime now,
            PlayerData playerData,
            List<PlayerData> hourPushed,
            List<PlayerData> eveningPushed) {
        TournamentEntity tournament = pn.getTournament();
        if (tournament == null || tournament.getDate() == null) return;

        if (tournament.getDate().equals(today)) {
            reminderNotificationSender.sendHourReminder(playerData, now, hourPushed,pn);
        }
        if (tournament.getDate().equals(tomorrow)) {
            reminderNotificationSender.sendEveningReminder(playerData, pn, now, eveningPushed);

        }
    }

//    private void sendHourReminder(PlayerData playerData,
//                                  PlayerNotification pn,
//                                  LocalTime now,
//                                  List<PlayerData> hourPushed) {
//        String time = pn.getTournament().getTime();
//        if (time == null || time.isEmpty()) return;
//
//        Long minutes = DateTimeUtils.parseMinutesUntil(time, now);
//        if (minutes == null || minutes <= 0 || minutes > 60) return;
//
//        reminderNotificationSender.sendHourReminder(playerData, time, minutes, hourPushed, pn);
//    }


}