package ru.pulsecore.app.notification.application.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.infrastructure.config.NotificationAsyncConfig;
import ru.pulsecore.app.shared.event.SingleMailEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSingleProcessor {

    private final MailStrategyRegistry mailStrategyRegistry;

    @Async(NotificationAsyncConfig.MAIL_EXECUTOR)
    public void process(SingleMailEvent event) {
        mailStrategyRegistry.send(event.content().emailType(), event.content().context());
    }
}