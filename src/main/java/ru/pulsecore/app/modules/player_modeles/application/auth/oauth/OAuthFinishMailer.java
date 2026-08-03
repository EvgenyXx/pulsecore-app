package ru.pulsecore.app.modules.player_modeles.application.auth.oauth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player_modeles.entity.Player;
import ru.pulsecore.app.modules.shared.properties.AdminProperties;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.notification_modules.application.mail.MailTypes;
import ru.pulsecore.app.modules.notification_modules.application.mail.context.AdminNewUserContext;
import ru.pulsecore.app.modules.notification_modules.application.mail.context.WelcomeContext;
import ua_parser.Client;
import ua_parser.Parser;

@Component
@RequiredArgsConstructor
public class OAuthFinishMailer {

    private final MailStrategyRegistry mailStrategyRegistry;
    private final AdminProperties adminProperties;
    private final Parser uaParser;

    public void sendWelcome(Player player) {
        if (player.getEmail() != null && player.getEmail().contains("@")) {
            mailStrategyRegistry.send(MailTypes.WELCOME,
                    new WelcomeContext(player.getEmail(), player.getName()));
        }
    }

    public void notifyAdmin(Player player, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent") != null ?
                request.getHeader("User-Agent") : "Неизвестно";
        Client client = uaParser.parse(userAgent);

        mailStrategyRegistry.send(MailTypes.ADMIN_NEW_USER,
                new AdminNewUserContext(
                        adminProperties.getEmail(),
                        player.getName(),
                        player.getEmail() != null ? player.getEmail() : "нет",
                        ip,
                        client.device.family,
                        client.os.family,
                        client.userAgent.family,
                        userAgent));
    }
}