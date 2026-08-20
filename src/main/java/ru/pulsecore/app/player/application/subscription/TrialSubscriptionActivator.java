package ru.pulsecore.app.player.application.subscription;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.player.domain.Subscription;

/**
 * Выдача пробная подписка одна неделя
 */
@Component
@RequiredArgsConstructor
public class TrialSubscriptionActivator {

    private static final int TRIAL_DAYS = 7;


    private final SubscriptionCommandService subscriptionCommandService;


    public void execute(Player player) {
        Subscription trial = Subscription.builder().player(player).build();
        trial.activate(TRIAL_DAYS);
        subscriptionCommandService.save(trial);

    }


}