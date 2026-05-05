package ru.pulsecore.app.modules.player.api;

import ru.pulsecore.app.modules.player.service.SubscriptionService;
import ru.pulsecore.app.modules.player.service.strategy.MailStrategyRegistry;
import ru.pulsecore.app.modules.player.service.strategy.MailTypes;
import ru.pulsecore.app.modules.shared.AdminProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final SubscriptionService subscriptionService;
    private final MailStrategyRegistry mailStrategyRegistry;
    private final AdminProperties adminProperties;

    private static final String WEBHOOK_PATH = "/api/payment/webhook";
    private static final String EVENT_SUCCEEDED = "payment.succeeded";
    private static final String KEY_EVENT = "event";
    private static final String KEY_OBJECT = "object";
    private static final String KEY_METADATA = "metadata";
    private static final String KEY_PLAYER_ID = "playerId";
    private static final String KEY_MONTHS = "months";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_VALUE = "value";
    private static final String KEY_CURRENCY = "currency";
    private static final int DAYS_PER_MONTH = 30;

    @PostMapping(WEBHOOK_PATH)
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> body) {
        log.info("Webhook received: {}", body);

        try {
            if (!EVENT_SUCCEEDED.equals(body.get(KEY_EVENT))) {
                return ResponseEntity.ok("ignored");
            }

            Map<String, Object> payment = getNestedMap(body, KEY_OBJECT);
            Map<String, Object> metadata = getNestedMap(payment, KEY_METADATA);

            if (!metadata.containsKey(KEY_PLAYER_ID)) {
                return ResponseEntity.ok("ignored");
            }

            UUID playerId = UUID.fromString((String) metadata.get(KEY_PLAYER_ID));
            int months = Integer.parseInt((String) metadata.get(KEY_MONTHS));

            subscriptionService.activate(playerId, months * DAYS_PER_MONTH);
            log.info("Subscription activated: playerId={}, months={}", playerId, months);


            Map<String, Object> amountMap = getNestedMap(payment, KEY_AMOUNT);
            String amount = (String) amountMap.get(KEY_VALUE);
            String currency = (String) amountMap.get(KEY_CURRENCY);


            mailStrategyRegistry.send(MailTypes.ADMIN_PAYMENT_RECEIVED,
                    adminProperties.getEmail(),
                    playerId.toString(), months, amount, currency);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            log.error("Webhook error", e);
            return ResponseEntity.ok("error");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getNestedMap(Map<String, Object> source, String key) {
        return (Map<String, Object>) source.get(key);
    }
}