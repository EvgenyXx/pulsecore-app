package ru.pulsecore.app.modules.shared.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.shared.properties.AdminProperties;
import ru.pulsecore.app.modules.shared.service.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.shared.service.mail.MailTypes;
import ru.pulsecore.app.modules.shared.service.mail.context.AdminNewUserContext;
import ru.pulsecore.app.modules.shared.service.mail.context.VerificationContext;
import ru.pulsecore.app.modules.shared.service.mail.context.WelcomeContext;
import ua_parser.Client;
import ua_parser.Parser;

@Component
@RequiredArgsConstructor
public class RegistrationMailService {

    private final MailStrategyRegistry mail;
    private final AdminProperties adminProperties;
    private final Parser uaParser;

    public void sendVerificationCode(String email, String code) {
        mail.send(MailTypes.VERIFICATION, new VerificationContext(email, code));
    }

    public void sendWelcome(Player player) {
        if (player.getEmail() != null && player.getEmail().contains("@")) {
            mail.send(MailTypes.WELCOME, new WelcomeContext(player.getEmail(), player.getName()));
        }
    }

    public void notifyAdminNewUser(Player player, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "Неизвестно";
        Client client = uaParser.parse(userAgent);

        mail.send(MailTypes.ADMIN_NEW_USER,
                new AdminNewUserContext(
                        adminProperties.getEmail(),
                        player.getName(),
                        player.getEmail(),
                        ip,
                        client.device.family,
                        client.os.family,
                        client.userAgent.family,
                        userAgent));
    }
}