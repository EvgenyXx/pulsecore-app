package ru.pulsecore.app.player.application.admin;


import com.ibm.icu.text.Transliterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.application.auth.RegistrationValidator;
import ru.pulsecore.app.player.application.player.PlayerCommandService;
import ru.pulsecore.app.player.application.subscription.TrialSubscriptionActivator;
import ru.pulsecore.app.player.domain.Player;
import ru.pulsecore.app.shared.api.player.CreatePlayerRequest;
import ru.pulsecore.app.shared.event.PlayerCreatedEvent;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerCreateAdminService {

    private static final int RECENT_DAYS = 30;
    private static final String EMAIL_DOMAIN = "@yandex.ru";
    private static final String DEFAULT_PASS = "123456";
    private static final Transliterator TRANSLITERATOR =
            Transliterator.getInstance("Russian-Latin/BGN");


    private final PlayerCommandService playerCommandService;
    private final ApplicationEventPublisher eventPublisher;
    private final TrialSubscriptionActivator trialSubscriptionActivator;
    private final RegistrationValidator validator;

    @Transactional
    public void createPlayer(CreatePlayerRequest request) {
        String email = createEmail(request.name());
        validator.validate(email, request.name());
        Player player = playerCommandService.createForPlayer(request.name(), email,DEFAULT_PASS);
        trialSubscriptionActivator.execute(player);
        eventPublisher.publishEvent(
                new PlayerCreatedEvent(
                        player.getId(),
                        player.getName(),
                        player.getEmail(),
                        RECENT_DAYS,
                        null,
                        null,
                        true
                )
        );

    }

    private String createEmail(String name) {
        String latin = TRANSLITERATOR.transliterate(name);

        String local = latin.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", ".")
                .replaceAll("^\\.+|\\.+$", "");
        return local + EMAIL_DOMAIN;
    }
}
