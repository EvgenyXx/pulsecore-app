package ru.pulsecore.app.notification.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailBatchProcessor;
import ru.pulsecore.app.notification.application.mail.MailSingleProcessor;
import ru.pulsecore.app.shared.event.MailBatchEvent;
import ru.pulsecore.app.shared.event.SingleMailEvent;

@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final MailBatchProcessor mailBatchProcessor;
    private final MailSingleProcessor mailSingleProcessor;

    @EventListener
    public void handleBatch(MailBatchEvent event) {
        mailBatchProcessor.process(event);
    }

    @EventListener
    public void handleSingle(SingleMailEvent event) {
        mailSingleProcessor.process(event);
    }
}