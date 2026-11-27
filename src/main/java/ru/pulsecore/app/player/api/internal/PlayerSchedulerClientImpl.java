package ru.pulsecore.app.player.api.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.application.player.PlayerCityService;
import ru.pulsecore.app.shared.api.player.PlayerSchedulerClient;


@Component
@RequiredArgsConstructor
public class PlayerSchedulerClientImpl implements PlayerSchedulerClient {

     private final PlayerCityService playerCityService;


    @Override
    public void syncUpcomingCities() {
        playerCityService.syncUpcomingCities();
    }
}
