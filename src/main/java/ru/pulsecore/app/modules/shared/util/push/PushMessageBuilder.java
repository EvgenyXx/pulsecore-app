// PushMessageBuilder.java
package ru.pulsecore.app.modules.shared.util.push;

import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.shared.util.DateTimeUtils;
import ru.pulsecore.app.modules.shared.util.StringUtils;

import java.util.List;

public class PushMessageBuilder {

    private PushMessageBuilder() {}

    public static String buildNewTournamentBody(String playerName, TournamentDto t) {
        String firstName = StringUtils.extractFirstName(playerName);
        String dateStr = DateTimeUtils.formatDate(t.getDate() != null ? t.getDate().getDate() : null);
        String timeStr = DateTimeUtils.formatTime(t.getDate() != null ? t.getDate().getDate() : null);
        String hall = t.getHall() != null ? t.getHall() : "—";
        String league = t.getLeague() != null ? t.getLeague() : "—";

        StringBuilder body = new StringBuilder();
        body.append(firstName).append(", вы записаны на турнир!\n\n");
        body.append("📅 ").append(dateStr).append(" в ").append(timeStr).append("\n");
        body.append("🏛 Зал: ").append(hall).append("\n");
        body.append("🏆 Лига: ").append(league).append("\n\n");

        List<String> players = t.getPlayers();
        if (players != null && !players.isEmpty()) {
            body.append("👥 Состав:\n");
            int count = Math.min(players.size(), 10);
            for (int i = 0; i < count; i++) {
                body.append(i + 1).append(". ").append(players.get(i)).append("\n");
            }
            if (players.size() > 10) {
                body.append("... и ещё ").append(players.size() - 10).append("\n");
            }
        }
        return body.toString();
    }

    public static String buildCancelledBody(String date, String time) {
        return "Турнир " + date + " в " + time + " был отменён.\n\nPulseCore";
    }


    public static final String SUBSCRIPTION_EXPIRING_BODY = """
        Завтра истекает срок действия подписки.
        
        🔕 Push-уведомления будут отключены.
        💳 Продлите подписку, чтобы продолжить получать уведомления о турнирах.
        
        PulseCore""";

    public static String buildHourReminderBody(String time, long minutes) {
        return "Начало в " + time + ". До старта " + minutes + " мин. Проверьте состав!\n\nPulseCore";
    }

    public static String buildEveningReminderBody(String time) {
        return "Завтра в " + (time != null ? time : "?") + ". Проверьте состав и будьте готовы!";
    }
}