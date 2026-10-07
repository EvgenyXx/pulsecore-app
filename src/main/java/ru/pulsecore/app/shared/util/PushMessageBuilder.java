package ru.pulsecore.app.shared.util;

import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.domain.enums.LineupType;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;
import java.util.List;

public class PushMessageBuilder {

    private PushMessageBuilder() {
    }

    public static String buildNewTournamentBody(String playerName, List<TournamentDto> tournaments) {
    if (tournaments == null || tournaments.isEmpty()) return "";
    if (tournaments.size() == 1) return buildNewTournamentBody(playerName, tournaments.get(0));

    String firstName = StringUtils.extractFirstName(playerName);
    StringBuilder body = new StringBuilder();
    body.append(firstName).append(", вы записаны на ").append(tournaments.size()).append(" турнира:\n\n");

    for (TournamentDto t : tournaments) {
        String dateStr = DateTimeUtils.formatDate(t.getDate() != null ? t.getDate().getDate() : null);
        String timeStr = DateTimeUtils.formatTime(t.getDate() != null ? t.getDate().getDate() : null);
        String hall = t.getHall() != null ? t.getHall() : "—";

        body.append("• ").append(dateStr).append(" ").append(timeStr)
            .append(" — ").append(hall).append("\n");
    }
    return body.toString();
}

    public static String buildNewTournamentBody(String playerName, TournamentDto t) {
        String firstName = StringUtils.extractFirstName(playerName);
        String dateStr = DateTimeUtils.formatDate(t.getDate() != null ? t.getDate().getDate() : null);
        String timeStr = DateTimeUtils.formatTime(t.getDate() != null ? t.getDate().getDate() : null);
        String hall = t.getHall() != null ? t.getHall() : "—";
        String league = t.getLeague() != null ? t.getLeague() : "—";
        String format = LineupType.fromApiType(t.getType()).displayName();

        StringBuilder body = new StringBuilder();
        body.append(firstName).append(", вы записаны на турнир!\n\n");
        body.append("Дата: ").append(dateStr).append(" в ").append(timeStr).append("\n");
        body.append("Зал: ").append(hall).append("\n");
        body.append("Лига: ").append(league).append("\n");
        body.append("Формат: ").append(format).append("\n\n");

        List<String> players = t.getPlayers();
        if (players != null && !players.isEmpty()) {
            body.append("Состав:\n");
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
        return "Турнир отменён\n\n"
                + "Дата: " + date + "\n"
                + "Время: " + time + "\n\n"
                + "PulseCore";
    }


    public static final String SUBSCRIPTION_EXPIRING_BODY = """
            Завтра истекает срок действия подписки.
            
             Push-уведомления будут отключены.
             Продлите подписку, чтобы продолжить получать уведомления о турнирах.
            
            PulseCore""";

    public static String buildHourReminderBody(String time) {
        return "Начало в " + time + ".";
    }

    public static String buildEveningReminderBody(String time) {
        return "Начало в " + time + ".";
    }


}