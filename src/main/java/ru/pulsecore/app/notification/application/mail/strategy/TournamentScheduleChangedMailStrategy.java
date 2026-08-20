package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;
import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.TournamentScheduleChangedContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;
import ru.pulsecore.app.tournament.application.roster.change.TransferInfo;
import ru.pulsecore.app.tournament.infrastructure.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TournamentScheduleChangedMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.TOURNAMENT_SCHEDULE_CHANGED;
    }

    @Override
    public void send(MailContext ctx) {
        TournamentScheduleChangedContext c = (TournamentScheduleChangedContext) ctx;
        TransferInfo info = c.transferInfo();

        String text = templates.format(
                MailTemplate.TOURNAMENT_SCHEDULE_CHANGED,
                c.firstName(),
                // Было
                info.from().getLeague(),
                DateTimeUtils.formatDate(info.from().getDate().getDate()),
                DateTimeUtils.formatTime(info.from().getDate().getDate()),
                info.from().getHall(),
                info.from().getPlayers() != null ? String.join(", ", info.from().getPlayers()) : "—",
                info.from().getLink(),
                // Стало
                info.to().getLeague(),
                DateTimeUtils.formatDate(info.to().getDate().getDate()),
                DateTimeUtils.formatTime(info.to().getDate().getDate()),
                info.to().getHall(),
                info.to().getPlayers() != null ? String.join(", ", info.to().getPlayers()) : "—",
                info.to().getLink(),
                // Изменения
                buildChanges(info)
        );

        mailSender.send(
                MailFormat.TEXT,
                c.to(),
                "📅 Изменение расписания турнира",
                text,
                null,
                null);
    }

    private String buildChanges(TransferInfo info) {
        List<String> changes = new ArrayList<>();
        if (info.timeChanged()) changes.add("время");
        if (info.dateChanged()) changes.add("дата");
        if (info.leagueChanged()) changes.add("лига");
        if (info.hallChanged()) changes.add("зал");
        return changes.isEmpty() ? "Без изменений" : String.join(", ", changes);
    }
}