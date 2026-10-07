package ru.pulsecore.app.notification.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.PushNotificationProcessor;
import ru.pulsecore.app.shared.event.PushNotificationEvent;


@Component
@RequiredArgsConstructor
@Slf4j
public class PushNotificationListener {

   private final PushNotificationProcessor pushNotificationProcessor;


    @EventListener
    public void sendPush(PushNotificationEvent event) {
        pushNotificationProcessor.dispatch(event);
    }
}
