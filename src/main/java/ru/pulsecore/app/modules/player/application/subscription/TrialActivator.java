package ru.pulsecore.app.modules.player.application.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.entity.Player;
import ru.pulsecore.app.modules.player.entity.Subscription;
import ru.pulsecore.app.modules.player.infrastructure.persistence.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class TrialActivator {

    private static final int TRIAL_DAYS = 7;

    private final SubscriptionRepository subscriptionRepository;

    public void activate(Player player) {
        Subscription trial = Subscription.builder().player(player).build();
        trial.activate(TRIAL_DAYS);
        subscriptionRepository.save(trial);
    }
}