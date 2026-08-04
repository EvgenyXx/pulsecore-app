package ru.pulsecore.app.modules.notification_modules.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailTypes;
import ru.pulsecore.app.modules.notification_modules.application.mail.context.AdminNewUserContext;
import ru.pulsecore.app.modules.notification_modules.application.mail.context.WelcomeContext;
import ru.pulsecore.app.modules.shared.dto.PlayerCreatedEvent;
import ru.pulsecore.app.modules.notification_modules.infrastructure.properties.AdminProperties;
import ua_parser.Client;
import ua_parser.Parser;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerCreatedHandler {

    private final MailStrategyRegistry mailStrategyRegistry;
    private final AdminProperties adminProperties;
    private final Parser uaParser;


    public void process(PlayerCreatedEvent event) {
        log.info("Player created: playerId={}", event.playerId());

        // Welcome
        if (event.email() != null && !event.email().isBlank()) {
            mailStrategyRegistry.send(MailTypes.WELCOME,
                    new WelcomeContext(event.email(), event.playerName()));
        }

        // Admin
        String agent = event.userAgent() != null ? event.userAgent() : "Неизвестно";
        Client client = uaParser.parse(agent);
        mailStrategyRegistry.send(MailTypes.ADMIN_NEW_USER,
                new AdminNewUserContext(
                        adminProperties.getEmail(),
                        event.playerName(),
                        event.email(),
                        event.ip(),
                        client.device.family,
                        client.os.family,
                        client.userAgent.family,
                        agent
                ));

    }
}
