package ru.pulsecore.app.modules.player.application.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.entity.Player;
import ru.pulsecore.app.modules.player.entity.Subscription;
import ru.pulsecore.app.modules.player.infrastructure.persistence.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class PostRegistrationService {

    private static final int TRIAL_DAYS = 7;


    private final SubscriptionRepository subscriptionRepository;


    public void execute(Player player) {
        Subscription trial = Subscription.builder().player(player).build();
        trial.activate(TRIAL_DAYS);
        subscriptionRepository.save(trial);

    }


}