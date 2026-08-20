package ru.pulsecore.app.notification.infrastructure.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.notification.application.mail.MailTemplateService;
import ru.pulsecore.app.notification.application.mail.UniversalMailSender;
import ru.pulsecore.app.notification.application.mail.template.MailFormat;
import ru.pulsecore.app.notification.application.mail.template.MailTemplate;
import ru.pulsecore.app.notification.client.PlayerClient;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.shared.event.PaymentSuccessEvent;
import ru.pulsecore.app.tournament.infrastructure.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSuccessListener {

    private final PlayerClient playerClient;
    private final UniversalMailSender mailSender;
    private final MailTemplateService templates;

    @EventListener
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        PlayerData player = playerClient.getPlayer(event.playerId());

        sendUserPaymentEmail(player, event);
        sendAdminPaymentEmail(player, event);

        log.info("Письма об оплате отправлены: playerId={}, email={}",
                event.playerId(), player.email());
    }

    private void sendUserPaymentEmail(PlayerData player, PaymentSuccessEvent event) {
        String text = templates.format(
                MailTemplate.PAYMENT_SUCCESS_USER,
                StringUtils.extractFirstName(player.playerName()), event.days() / 30
        );
        mailSender.send(
                MailFormat.TEXT, player.email(),
                "Успешная оплата",
                text, null, null
        );
    }

    private void sendAdminPaymentEmail(PlayerData player, PaymentSuccessEvent event) {
       String text = templates.format(
               MailTemplate.ADMIN_PAYMENT,
               player.playerName(),player.email(),
               event.days() / 30,
               event.amount(),event.currency(),
               LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

       );
       mailSender.adminSendEmail(
               MailFormat.TEXT,
               "Продление подписки",
               text, null, null
       );
    }




}
