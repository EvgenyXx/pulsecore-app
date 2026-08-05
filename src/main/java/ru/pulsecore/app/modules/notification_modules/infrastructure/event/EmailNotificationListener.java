package ru.pulsecore.app.modules.notification_modules.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.shared.dto.MailNotificationEvent;


@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final MailStrategyRegistry mailStrategyRegistry;


    @EventListener
    public void handle(MailNotificationEvent events) {
        log.info("Email уведомление {}", events.getEmailType());
        try {
            mailStrategyRegistry.send(events.getEmailType(), events.getContextMessage());
        } catch (Exception e) {
            log.error("Ошибка при отправке {}", e.getMessage());
        }
    }

}