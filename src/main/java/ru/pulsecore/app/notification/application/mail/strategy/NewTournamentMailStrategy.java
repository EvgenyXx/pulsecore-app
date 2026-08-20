package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;

import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.NewTournamentContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;

@Component
@RequiredArgsConstructor
public class NewTournamentMailStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.NEW_TOURNAMENT;
    }

    @Override
    public void send(MailContext ctx) {
        NewTournamentContext c = (NewTournamentContext) ctx;
        String text = templates.format(MailTemplate.NEW_TOURNAMENT,
                c.firstName(), c.date(), c.time(), c.hall(),
                c.league(), c.players(), c.link());
        mailSender.send(
                MailFormat.TEXT,
                c.to(),
                "🏓 " + c.firstName() + ", новый турнир",
                text,
                null,
                null);
    }
}