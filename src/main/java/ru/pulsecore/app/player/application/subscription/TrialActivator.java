package ru.pulsecore.app.player.application.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Subscription;
import ru.pulsecore.app.player.infrastructure.persistence.repository.SubscriptionRepository;

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