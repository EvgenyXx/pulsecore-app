package ru.pulsecore.app.modules.shared.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.core.dto.TournamentDto;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.shared.util.DateTimeUtils;
import ru.pulsecore.app.modules.shared.util.StringUtils;

@Component
@RequiredArgsConstructor
public class NewTournamentMailStrategy implements MailStrategy {

    private final MailProperties mailProperties;

    @Override
    public String getType() {
        return MailTypes.NEW_TOURNAMENT;
    }

    @Override
    public SimpleMailMessage createMessage(String to, Object... args) {
        TournamentDto tournament = (TournamentDto) args[0];
        Player player = (Player) args[1];

        String firstName = StringUtils.extractFirstName(player.getName());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(to);
        message.setSubject("🏓 " + firstName + ", новый турнир");
        message.setText(getTextBody(tournament, firstName));

        return message;
    }

    private static String getTextBody(TournamentDto tournament, String firstName) {
        String rawDate = tournament.getDate() != null ? tournament.getDate().getDate() : null;
        String dateStr = DateTimeUtils.formatDate(rawDate);
        String timeStr = DateTimeUtils.formatTime(rawDate);
        String hall = tournament.getHall() != null ? tournament.getHall() : "—";
        String league = tournament.getLeague() != null ? tournament.getLeague() : "—";
        String link = tournament.getLink() != null ? tournament.getLink() : "";

        StringBuilder sb = new StringBuilder();
        sb.append(firstName).append(", вы записаны на турнир!\n\n");
        sb.append("📅 Дата: ").append(dateStr).append("\n");
        sb.append("⏰ Время: ").append(timeStr).append("\n");
        sb.append("🏛 Зал: ").append(hall).append("\n");
        sb.append("🏆 Лига: ").append(league).append("\n\n");

        if (tournament.getPlayers() != null && !tournament.getPlayers().isEmpty()) {
            sb.append("👥 Состав участников:\n");
            for (int i = 0; i < tournament.getPlayers().size(); i++) {
                sb.append(i + 1).append(". ").append(tournament.getPlayers().get(i)).append("\n");
            }
            sb.append("\n");
        }

        if (!link.isEmpty()) {
            sb.append("🔗 Турнир: ").append(link).append("\n");
        }
        sb.append("📊 Результаты: https://pulsecore-app.ru\n");
        sb.append("👤 Личный кабинет: https://pulsecore-app.ru/dashboard");

        return sb.toString();
    }
}