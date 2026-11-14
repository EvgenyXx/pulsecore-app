package ru.pulsecore.app.notification.application.mail;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.infrastructure.config.NotificationAsyncConfig;
import ru.pulsecore.app.notification.infrastructure.properties.AdminProperties;
import ru.pulsecore.app.notification.application.mail.sender.MailSendStrategy;
import ru.pulsecore.app.notification.application.mail.sender.PdfMailSender;
import ru.pulsecore.app.notification.application.mail.sender.TextMailSender;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.infrastructure.properties.MailProperties;

import java.util.Map;


@Component
public class UniversalMailSender {

    private final Map<MailFormat, MailSendStrategy> senders;
    private final MailProperties mailProperties;
    private final AdminProperties adminProps;


    public UniversalMailSender(TextMailSender text, PdfMailSender pdf, MailProperties props, AdminProperties adminProps) {
        this.mailProperties = props;
        this.adminProps = adminProps;
        this.senders = Map.of(
                MailFormat.TEXT, text,
                MailFormat.PDF, pdf
        );
    }

    @Async(NotificationAsyncConfig.MAIL_EXECUTOR)
    public void send(MailFormat format,
                     String to, String subject,
                     String text, String fileName,
                     byte[] attachment) {
        senders.get(format).send(mailProperties.from(), to, subject, text, fileName, attachment);
    }

    public void adminSendEmail(
            MailFormat format,
            String subject, String text,
            String fileName, byte[] attachment) {
        senders.get(format).send(mailProperties.from(), adminProps.email(), subject, text, fileName, attachment);

    }
}