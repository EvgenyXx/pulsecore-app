package ru.pulsecore.app.notification.application.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;
import ru.pulsecore.app.notification.application.mail.context.BrokenUriContext;
import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;


@Component
@RequiredArgsConstructor
public class BrokenUriStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @Override
    public String getType() {
        return MailTypes.BROKEN_URI;
    }

    @Override
    public void send(MailContext context) {
        BrokenUriContext brokenUriContext =  (BrokenUriContext) context;
        String tex = templates.format(
                MailTemplate.BROKEN_URI,brokenUriContext.brokenUri());
        mailSender.send(
                MailFormat.TEXT,
                brokenUriContext.to(),
                "Битый ссылка на турнир",
                tex,
                null,
                null

        );

    }
}
