// 4. OAuthFinishMailer.java
package ru.pulsecore.app.modules.auth.service.finish;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.modules.player.domain.Player;
import ru.pulsecore.app.modules.shared.properties.AdminProperties;
import ru.pulsecore.app.modules.shared.service.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.shared.service.mail.MailTypes;
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
            mailStrategyRegistry.send(MailTypes.WELCOME, player.getEmail(), player.getName());
        }
    }

    public void notifyAdmin(Player player, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent") != null ?
                request.getHeader("User-Agent") : "Неизвестно";
        Client client = uaParser.parse(userAgent);
        mailStrategyRegistry.send(MailTypes.ADMIN_NEW_USER,
                adminProperties.getEmail(),
                player.getName(), player.getEmail() != null ? player.getEmail() : "нет",
                ip, userAgent, client.device.family, client.os.family, client.userAgent.family);
    }
}