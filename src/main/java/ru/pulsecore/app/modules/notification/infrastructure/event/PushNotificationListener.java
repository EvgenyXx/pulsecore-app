package ru.pulsecore.app.modules.notification.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.notification.application.WebPushService;
import ru.pulsecore.app.modules.shared.dto.PushNotificationEvent;



@Component
@RequiredArgsConstructor
@Slf4j
public class PushNotificationListener {

    private  final WebPushService webPushService;


   @EventListener
    public void sendPush(PushNotificationEvent event) {
       log.info("Началась отправка пуш увд для {}",event.playerId());
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
