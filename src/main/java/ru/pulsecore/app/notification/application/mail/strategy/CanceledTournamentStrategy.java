package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;
import ru.pulsecore.app.notification.application.mail.context.CanceledTournamentContext;
import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;

@Component
@RequiredArgsConstructor
public class CanceledTournamentStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.CANCELED_TOURNAMENT;
    }

    @Override
    public void send(MailContext context) {
        CanceledTournamentContext ctx = (CanceledTournamentContext) context;
        String text = templates.format(
                MailTemplate.CANCELED_TOURNAMENT,ctx.data(),ctx.time(),ctx.link());
        mailSender.send(
                MailFormat.TEXT,ctx.to(),
                "Отмена турнира",
                text,
                null,
                null
        );
    }
}
