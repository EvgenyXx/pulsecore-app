package ru.pulsecore.app.notification.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategyRegistry;
import ru.pulsecore.app.shared.event.MailNotificationEvent;


@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final MailStrategyRegistry mailStrategyRegistry;

    @EventListener
    public void handle(MailNotificationEvent event) {
        try {
            mailStrategyRegistry.send(event.getEmailType(), event.getContextMessage());
            log.info("Письмо отправлено: type={}", event.getEmailType());
        } catch (Exception e) {
            log.error("Ошибка при отправке письма: type={}, error={}",
                    event.getEmailType(), e.getMessage());
        }
    }
}