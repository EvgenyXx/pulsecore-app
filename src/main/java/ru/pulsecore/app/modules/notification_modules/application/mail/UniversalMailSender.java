package ru.pulsecore.app.modules.notification_modules.application.mail;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.shared.config.AsyncConfig;
import ru.pulsecore.app.modules.notification_modules.application.mail.sender.MailSendStrategy;
import ru.pulsecore.app.modules.notification_modules.application.mail.sender.PdfMailSender;
import ru.pulsecore.app.modules.notification_modules.application.mail.sender.TextMailSender;
import ru.pulsecore.app.modules.notification_modules.application.mail.template.MailFormat;


import java.util.Map;


@Component
public class UniversalMailSender {

    private final Map<MailFormat, MailSendStrategy> senders;
    private final MailProperties props;


    public UniversalMailSender(TextMailSender text, PdfMailSender pdf, MailProperties props) {
        this.props = props;
        this.senders = Map.of(
                MailFormat.TEXT, text,
                MailFormat.PDF, pdf
        );
    }

    @Async(AsyncConfig.MAIL_EXECUTOR)
    public void send(MailFormat format,
                     String to, String subject,
                     String text, String fileName,
                     byte[] attachment) {
        senders.get(format).send(props.getFrom(),to, subject, text, fileName, attachment);
    }
}