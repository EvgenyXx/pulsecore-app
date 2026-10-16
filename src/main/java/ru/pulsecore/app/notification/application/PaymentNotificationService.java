package ru.pulsecore.app.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;
import ru.pulsecore.app.notification.client.PlayerClient;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.SubscriptionWebhookEvent;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentNotificationService {

    private final PlayerClient playerClient;
    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    public void sendSuccess(SubscriptionWebhookEvent event) {
        PlayerData player = playerClient.getPlayer(event.playerId());

        sendUserSuccessEmail(player, event);
        sendAdminSuccessEmail(player, event);

        log.info("Письма об оплате отправлены: id={}, email={}",
                event.playerId(), player.email());
    }


    private void sendUserSuccessEmail(PlayerData player, SubscriptionWebhookEvent event) {
        String text = templates.format(
                MailTemplate.PAYMENT_SUCCESS_USER,
                StringUtils.extractFirstName(player.name()),
                event.month()
        );
        mailSender.send(
                MailFormat.TEXT, player.email(),
                "Успешная оплата",
                text, null, null
        );
        log.debug("Письмо пользователю отправлено на {}", player.email());
    }

    private void sendAdminSuccessEmail(PlayerData player, SubscriptionWebhookEvent event) {
        String text = templates.format(
                MailTemplate.ADMIN_PAYMENT,
                player.name(), player.email(),
                event.month(),
                event.amount(), event.currency(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        );
        mailSender.adminSendEmail(
                MailFormat.TEXT,
                "Продление подписки",
                text, null, null
        );
        log.debug("Письмо админу отправлено");
    }
}