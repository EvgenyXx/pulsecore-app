package ru.pulsecore.app.notification.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.WebPushService;
import ru.pulsecore.app.shared.event.PushNotificationEvent;



@Component
@RequiredArgsConstructor
@Slf4j
public class PushNotificationListener {

    private  final WebPushService webPushService;


   @EventListener
    public void sendPush(PushNotificationEvent event) {
           try {
               webPushService.sendToPlayer(
                       event.playerId(),
                       event.title(),
                       event.body(),event.url()
               );
           }catch (Exception e) {
               log.error("Ошибка при отправке пушем {}",e.getMessage());
           }

    }
}
