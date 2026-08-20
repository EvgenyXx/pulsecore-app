package ru.pulsecore.app.notification.application.mail.strategy.admin;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;
import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.context.admin.MastersApiUnavailableContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;

@Component
@RequiredArgsConstructor
public class MastersApiUnavailableStrategy implements MailStrategy {

    private final UniversalMailSender  universalMailSender;

    @Override
    public String getType() {
        return MailTypes.ADMIN_MASTERS_UNAVAILABLE;
    }

    @Override
    public void send(MailContext context) {
        MastersApiUnavailableContext unavailableContext = (MastersApiUnavailableContext) context;
        universalMailSender.adminSendEmail(
                MailFormat.TEXT,
                "Мастерс блок",
                unavailableContext.text(),
                null,
                null);
    }
}
