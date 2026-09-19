package ru.pulsecore.app.tournament.application.roster.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Базовый класс для региональных напоминаний.
 * Содержит общую логику: загрузка уведомлений, расчёт времени,
 * проверка и отправка push-уведомлений.
 * Подклассы переопределяют: getHalls(), getZone(), getRegionName().
 */
@Slf4j
@RequiredArgsConstructor
public abstract class RegionalReminderService {

    private final PlayerNotificationRepository notificationRepository;
    private final PlayerClient playerClient;
    private final ReminderNotificationSender reminderNotificationSender;

    /**
     * Зал региона. Если null или пусто — все залы (fallback).
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
        processHourReminders();
        processEveningReminders();
    }

    // ==================== ЧАСОВЫЕ (турнир СЕГОДНЯ) ====================

    private void processHourReminders() {
        List<Integer> halls = getHalls();
        List<PlayerNotification> notifications = (halls == null || halls.isEmpty())
                ? notificationRepository.findPendingHourReminders()
                : notificationRepository.findPendingHourRemindersByHalls(halls);

        if (notifications.isEmpty()) return;

        Map<UUID, PlayerData> playerMap = loadPlayers(notifications);
        List<PlayerData> pushed = new ArrayList<>();

        notifications.forEach(pn -> {
            PlayerData player = playerMap.get(pn.getPlayerId());
            if (player != null) {
                reminderNotificationSender.sendHourReminder(
                        player, pn.getTournament().getTime(), pushed, pn);
            }
        });

        if (!pushed.isEmpty()) {
            log.info("{} — пуш за час отправлен: {}",
                    getRegionName(),
                    pushed.stream().map(PlayerData::playerName).collect(Collectors.joining(", ")));
        }
    }

    // ==================== ВЕЧЕРНИЕ (турнир ЗАВТРА) ====================

    private void processEveningReminders() {
        List<Integer> halls = getHalls();
        LocalDate tomorrow = LocalDate.now(getZone()).plusDays(1);

        List<PlayerNotification> notifications = (halls == null || halls.isEmpty())
                ? notificationRepository.findPendingEveningReminders(tomorrow)
                : notificationRepository.findPendingEveningRemindersByHalls(halls, tomorrow);

        if (notifications.isEmpty()) return;

        Map<UUID, PlayerData> playerMap = loadPlayers(notifications);
        LocalTime now = LocalTime.now(getZone()).withSecond(0).withNano(0);
        List<PlayerData> pushed = new ArrayList<>();

        notifications.forEach(pn -> {
            PlayerData player = playerMap.get(pn.getPlayerId());
            if (player != null) {
                reminderNotificationSender.sendEveningReminder(player, pn, now, pushed);
            }
        });

        if (!pushed.isEmpty()) {
            log.info("{} — вечерний пуш отправлен: {}",
                    getRegionName(),
                    pushed.stream().map(PlayerData::playerName).collect(Collectors.joining(", ")));
        }
    }

    // ==================== ОБЩЕЕ ====================

    private Map<UUID, PlayerData> loadPlayers(List<PlayerNotification> notifications) {
        Set<UUID> playerIds = notifications.stream()
                .map(PlayerNotification::getPlayerId)
                .collect(Collectors.toSet());

        return playerClient.getPlayerDataByIds(playerIds).stream()
                .collect(Collectors.toMap(PlayerData::playerId, p -> p));
    }
}