package ru.pulsecore.app.notification.application.mail.strategy.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailStrategy;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.MailTypes;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;
import ru.pulsecore.app.notification.application.mail.context.admin.BrokenUriContext;
import ru.pulsecore.app.notification.application.mail.context.MailContext;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;
import ru.pulsecore.app.notification.client.PlayerClient;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class BrokenUriStrategy implements MailStrategy {

    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;
    private final PlayerClient  playerClient;

    @Override
    public String getType() {
        return MailTypes.BROKEN_URI;
    }

    @Override
    public void send(MailContext context) {
        BrokenUriContext brokenUriContext =  (BrokenUriContext) context;
        String playerData = playerClient.getPlayers(brokenUriContext.playerIds())
                .stream()
                .map(PlayerData::playerName)
                .collect(Collectors.joining(","));

        String tex = templates.format(
                MailTemplate.BROKEN_URI,brokenUriContext.brokenUri(),
                brokenUriContext.date(),
                brokenUriContext.time(),
                "LEGUA",
                playerData

                );
        mailSender.adminSendEmail(
                MailFormat.TEXT,
                "⚠️ Турнир удалён с сайта",
                tex,
                null,
                null

        );

    }
}
