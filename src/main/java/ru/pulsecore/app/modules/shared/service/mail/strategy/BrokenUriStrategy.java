package ru.pulsecore.app.modules.shared.service.mail.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.shared.service.mail.MailStrategy;
import ru.pulsecore.app.modules.shared.service.mail.MailTemplateService;
import ru.pulsecore.app.modules.shared.service.mail.MailTypes;
import ru.pulsecore.app.modules.shared.service.mail.UniversalMailSender;
import ru.pulsecore.app.modules.shared.service.mail.context.BrokenUriContext;
import ru.pulsecore.app.modules.shared.service.mail.context.MailContext;
import ru.pulsecore.app.modules.shared.service.mail.template.MailFormat;
import ru.pulsecore.app.modules.shared.service.mail.template.MailTemplate;


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
