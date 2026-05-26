package ru.pulsecore.app.modules.notification.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.notification.service.NotificationPermissionService;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.player.service.player.PlayerService;
import ru.pulsecore.app.modules.player.service.strategy.MailStrategyRegistry;
import ru.pulsecore.app.modules.player.service.strategy.MailTypes;
import ru.pulsecore.app.modules.push.service.WebPushService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentDiscoveryService {

    private final PlayerService userService;
    private final TournamentFinder finder;
    private final TournamentFilter filter;
    private final TournamentSaver saver;
    private final MailStrategyRegistry mailStrategyRegistry;
    private final WebPushService webPushService;
    private final NotificationPermissionService notificationPermissionService;

    public void checkNewTournaments(UUID playerId) {
        Player user = userService.findById(playerId);
        if (user == null) {
            log.warn("Player not found: {}", playerId);
            return;
        }

        List<TournamentDto> tournaments = finder.find(user);
        if (tournaments.isEmpty()) {
            return;
        }

        List<TournamentDto> newTournaments = filter.findNew(user, tournaments);
        if (newTournaments.isEmpty()) {
            return;
        }

        saver.save(user, newTournaments);

        // Почта — если разрешено
        if (notificationPermissionService.canSendEmail(user)) {
            newTournaments.forEach(tournament ->
                    mailStrategyRegistry.send(MailTypes.NEW_TOURNAMENT, user.getEmail(), tournament, user)
            );
        } else {
            log.info("🔕 Email notifications disabled for {}", user.getEmail());
        }

        // Push — если разрешено
        if (notificationPermissionService.canSendPush(user)) {
            newTournaments.forEach(tournament -> {
                String firstName = extractFirstName(user.getName());
                String rawDate = tournament.getDate() != null ? tournament.getDate().getDate() : null;
                String dateStr = formatDate(rawDate);
                String timeStr = formatTime(rawDate);
                String hall = tournament.getHall() != null ? tournament.getHall() : "—";
                String league = tournament.getLeague() != null ? tournament.getLeague() : "—";

                StringBuilder body = new StringBuilder();
                body.append(firstName).append(", вы записаны на турнир!\n\n");
                body.append("📅 ").append(dateStr).append(" в ").append(timeStr).append("\n");
                body.append("🏛 Зал: ").append(hall).append("\n");
                body.append("🏆 Лига: ").append(league).append("\n\n");

                if (tournament.getPlayers() != null && !tournament.getPlayers().isEmpty()) {
                    body.append("👥 Состав:\n");
                    List<String> players = tournament.getPlayers();
                    int count = Math.min(players.size(), 10);
                    for (int i = 0; i < count; i++) {
                        body.append(i + 1).append(". ").append(players.get(i)).append("\n");
                    }
                    if (players.size() > 10) {
                        body.append("... и ещё ").append(players.size() - 10).append("\n");
                    }
                }

                webPushService.sendToPlayer(
                        user.getId(),
                        "📋 Вы в составе!",
                        body.toString(),
                        "/dashboard"
                );
            });
        } else {
            log.info("🔕 Push notifications disabled for {}", user.getEmail());
        }

        log.info("📧📲 Sent {} notifications to {}", newTournaments.size(), user.getEmail());
    }

    private String extractFirstName(String fullName) {
        return fullName.contains(" ") ? fullName.substring(fullName.lastIndexOf(" ") + 1) : fullName;
    }

    private static String formatDate(String raw) {
        if (raw == null) return "—";
        try {
            LocalDateTime dt = LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            return raw.split(" ")[0];
        }
    }

    private static String formatTime(String raw) {
        if (raw == null) return "—";
        try {
            LocalDateTime dt = LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            return dt.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            try {
                return raw.split(" ")[1].substring(0, 5);
            } catch (Exception ex) {
                return "—";
            }
        }
    }
}