package ru.pulsecore.app.player.application.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Subscription;
import ru.pulsecore.app.player.infrastructure.properties.PlayerSettingProperties;
import ru.pulsecore.app.player.infrastructure.repository.SubscriptionRepository;


/**
 * Сервис для выдачи пробного периода подписки
 * используется при регистрации
 */
@Component
@RequiredArgsConstructor
public class TrialActivator {

    private final PlayerSettingProperties settingProperties;

    private final SubscriptionRepository subscriptionRepository;

    public void activate(Player player) {
        Subscription trial = Subscription.builder().player(player).build();
        trial.activate(settingProperties.subscription().trialDays());
        subscriptionRepository.save(trial);
    }
}