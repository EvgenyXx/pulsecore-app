package ru.pulsecore.app.player.application.subscription;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Subscription;
import ru.pulsecore.app.player.infrastructure.repository.SubscriptionRepository;

/**
 * Выдача пробная подписка одна неделя
 */
@Component
@RequiredArgsConstructor
public class TrialSubscriptionActivator {

    private static final int TRIAL_DAYS = 7;


    private final SubscriptionRepository subscriptionRepository;


    @Transactional
    public void execute(Player player) {
        Subscription trial = Subscription.builder().player(player).build();
        trial.activate(TRIAL_DAYS);
        subscriptionRepository.save(trial);

    }


}