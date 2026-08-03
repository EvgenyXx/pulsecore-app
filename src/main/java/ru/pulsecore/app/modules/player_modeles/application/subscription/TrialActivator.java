package ru.pulsecore.app.modules.player_modeles.application.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player_modeles.entity.Player;
import ru.pulsecore.app.modules.player_modeles.entity.Subscription;
import ru.pulsecore.app.modules.player_modeles.infrastructure.persistence.repository.SubscriptionRepository;

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