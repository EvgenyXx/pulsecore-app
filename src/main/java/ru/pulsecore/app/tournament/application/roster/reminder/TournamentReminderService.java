//package ru.pulsecore.app.tournament.application.roster.reminder;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import ru.pulsecore.app.shared.dto.response.PlayerData;
//import ru.pulsecore.app.shared.event.PushNotificationEvent;
//import ru.pulsecore.app.shared.util.PushMessageBuilder;
//import ru.pulsecore.app.tournament.domain.entity.PlayerNotification;
//import ru.pulsecore.app.tournament.domain.entity.TournamentEntity;
//import ru.pulsecore.app.tournament.infrastructure.client.PlayerClient;
//import ru.pulsecore.app.tournament.infrastructure.persistence.repository.PlayerNotificationRepository;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class TournamentReminderService {
//    // todo: заменить LocalDate.now()/LocalTime.now() на Clock бин для тестирования
//    private final PlayerNotificationRepository notificationRepository;
//    private final PlayerClient playerClient;
//    private final ApplicationEventPublisher publisher;
//
//
//    @Transactional
//    public void sendReminders() {
//        List<PlayerNotification> notifications = notificationRepository.findPendingWithTournament();
//        if (notifications.isEmpty()) return;
//
//        Set<UUID> playerIds = notifications.stream()
//                .map(PlayerNotification::getPlayerId)
//                .collect(Collectors.toSet());
//
//        Map<UUID, PlayerData> playerMap = playerClient.getPlayerDataByIds(playerIds).stream()
//                .collect(Collectors.toMap(PlayerData::playerId, p -> p));
//
//        LocalDate today = LocalDate.now();
//        LocalDate tomorrow = today.plusDays(1);
//        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
//
//        List<PlayerData> hourPushed = new ArrayList<>();
//        List<PlayerData> eveningPushed = new ArrayList<>();
//
//        notifications.forEach(pn -> {
//            PlayerData player = playerMap.get(pn.getPlayerId());
//            if (player != null) {
//                processNotification(pn, today, tomorrow, now, player, hourPushed, eveningPushed);
//            }
//        });
//        logPushes(hourPushed, eveningPushed);
//    }
//
//    private void logPushes(List<PlayerData> hours, List<PlayerData> evenings) {
//        if (!hours.isEmpty()) {
//            log.info("Пуш за час отправлен: {}",
//                    hours.stream().map(PlayerData::playerName).collect(Collectors.joining(", ")));
//        }
//        if (!evenings.isEmpty()) {
//            log.info("Вечерний пуш отправлен: {}",
//                    evenings.stream().map(PlayerData::playerName).collect(Collectors.joining(", ")));
//        }
//    }
//
//    private void processNotification(
//            PlayerNotification pn,
//            LocalDate today,
//            LocalDate tomorrow,
//            LocalTime now,
//            PlayerData playerData,
//            List<PlayerData> hourPushed,
//            List<PlayerData> eveningPushed) {
//        TournamentEntity tournament = pn.getTournament();
//        if (tournament == null || tournament.getDate() == null) return;
//
//        if (tournament.getDate().equals(today)) {
//            sendHourReminder(playerData, pn, now, hourPushed);
//        }
//        if (tournament.getDate().equals(tomorrow)) {
//            sendEveningReminder(playerData, pn, now, eveningPushed);
//        }
//    }
//
//    private void sendHourReminder(PlayerData playerData,
//                                  PlayerNotification pn,
//                                  LocalTime now,
//                                  List<PlayerData> hourPushed) {
//        String time = pn.getTournament().getTime();
//        if (time == null || time.isEmpty()) return;
//
//        Long minutes = parseMinutesUntil(time, now);
//        if (minutes == null || minutes <= 0 || minutes > 60) return;
//
//        pushHourNotification(playerData, time, minutes, hourPushed);
//        pn.setPushReminderSent(true);
//        notificationRepository.save(pn);
//    }
//
//    private Long parseMinutesUntil(String time, LocalTime now) {
//        try {
//            LocalTime tournamentTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
//            return java.time.Duration.between(now, tournamentTime).toMinutes();
//        } catch (Exception e) {
//            log.warn("Ошибка парсинга времени турнира: {}", time);
//            return null;
//        }
//    }
//
//    private void pushHourNotification(PlayerData player, String time, long minutes, List<PlayerData> hourPushed) {
//        if (player.notificationsEnabled()) {
//            hourPushed.add(player);
//            publisher.publishEvent(
//                    new PushNotificationEvent(
//                            player.playerId(),
//                            "🏆 Турнир начинается!",
//                            PushMessageBuilder.buildHourReminderBody(time, minutes),
//                            "/dashboard"
//                    )
//            );
//        }
//    }
//
//    private void sendEveningReminder(PlayerData player,
//                                     PlayerNotification pn,
//                                     LocalTime now,
//                                     List<PlayerData> eveningPushed) {
//        if (now.getHour() != 20 || pn.isPushEveningSent()) return;
//        if (player.pushEnabled()) {
//            eveningPushed.add(player);
//            String time = pn.getTournament().getTime();
//            publisher.publishEvent(
//                    new PushNotificationEvent(
//                            player.playerId(),
//                            "📅 Завтра турнир!",
//                            PushMessageBuilder.buildEveningReminderBody(time),
//                            "/dashboard"
//                    )
//            );
//        }
//        // todo: получается если только отправлено мы начинаем отслеживать ?
//        // тогда проще поднять и даже если уведомление выключено ставить статус отправлено
//        pn.setPushEveningSent(true);
//        notificationRepository.save(pn);
//    }
//}