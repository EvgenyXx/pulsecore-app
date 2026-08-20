package ru.pulsecore.app.shared.util;

import ru.pulsecore.app.shared.dto.response.TournamentDto;
import ru.pulsecore.app.tournament.application.roster.change.TransferInfo;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

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


    public static String buildPlayerReplacedBody(String playerName, TournamentDto t) {
        String firstName = StringUtils.extractFirstName(playerName);
        String dateStr = DateTimeUtils.formatDate(t.getDate() != null ? t.getDate().getDate() : null);
        String timeStr = DateTimeUtils.formatTime(t.getDate() != null ? t.getDate().getDate() : null);
        String hall = t.getHall() != null ? t.getHall() : "—";
        String league = t.getLeague() != null ? t.getLeague() : "—";

        StringBuilder body = new StringBuilder();
        body.append(firstName).append(", вы сняты с турнира!\n\n");
        body.append("📅 ").append(dateStr).append(" в ").append(timeStr).append("\n");
        body.append("🏛 Зал: ").append(hall).append("\n");
        body.append("🏆 Лига: ").append(league).append("\n\n");
        body.append("Состав турнира обновлён. Возможно, вас заменили другим игроком.\n\n");
        body.append("PulseCore");

        return body.toString();
    }

    public static String buildPlayerTransferredBody(String playerName, TransferInfo info) {
        String firstName = StringUtils.extractFirstName(playerName);

        TournamentDto from = info.from();
        TournamentDto to = info.to();

        String fromDate = DateTimeUtils.formatDate(from.getDate() != null ? from.getDate().getDate() : null);
        String fromTime = DateTimeUtils.formatTime(from.getDate() != null ? from.getDate().getDate() : null);
        String fromLeague = from.getLeague() != null ? from.getLeague() : "—";
        String fromHall = from.getHall() != null ? from.getHall() : "—";

        String toDate = DateTimeUtils.formatDate(to.getDate() != null ? to.getDate().getDate() : null);
        String toTime = DateTimeUtils.formatTime(to.getDate() != null ? to.getDate().getDate() : null);
        String toLeague = to.getLeague() != null ? to.getLeague() : "—";
        String toHall = to.getHall() != null ? to.getHall() : "—";

        StringBuilder body = new StringBuilder();
        body.append(firstName).append(", вы перенесены в другой турнир!\n\n");

        body.append("Было:\n");
        body.append("🏆 ").append(fromLeague).append("\n");
        body.append("📅 ").append(fromDate).append(" в ").append(fromTime).append("\n");
        body.append("🏛 Зал: ").append(fromHall).append("\n\n");

        body.append("Стало:\n");
        body.append("🏆 ").append(toLeague).append("\n");
        body.append("📅 ").append(toDate).append(" в ").append(toTime).append("\n");
        body.append("🏛 Зал: ").append(toHall).append("\n\n");

        if (info.hasAnyChange()) {
            body.append("Изменения:\n");
            if (info.timeChanged()) body.append("⏰ Время изменено\n");
            if (info.dateChanged()) body.append("📅 Дата изменена\n");
            if (info.leagueChanged()) body.append("🏆 Лига изменена\n");
            if (info.hallChanged()) body.append("🏛 Зал изменён\n");
            body.append("\n");
        }

        body.append("PulseCore");

        return body.toString();
    }

}