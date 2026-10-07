package ru.pulsecore.app.notification.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.PlayerCreatedHandler;
import ru.pulsecore.app.shared.event.PlayerCreatedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerCreatedListener {

    private final PlayerCreatedHandler playerCreatedHandler;



    @EventListener
    public void handle(PlayerCreatedEvent event) {
        playerCreatedHandler.process(event);
    }
}