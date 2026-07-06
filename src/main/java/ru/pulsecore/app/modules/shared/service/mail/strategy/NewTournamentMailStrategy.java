// NewTournamentMailStrategy.java
package ru.pulsecore.app.modules.shared.service.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.shared.service.mail.*;
import ru.pulsecore.app.modules.shared.service.mail.context.MailContext;
import ru.pulsecore.app.modules.shared.service.mail.context.NewTournamentContext;
import ru.pulsecore.app.modules.shared.service.mail.template.MailFormat;
import ru.pulsecore.app.modules.shared.service.mail.template.MailTemplate;

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