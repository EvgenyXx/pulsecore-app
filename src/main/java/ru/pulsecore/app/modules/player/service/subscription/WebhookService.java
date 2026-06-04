package ru.pulsecore.app.modules.player.service.subscription;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.pulsecore.app.modules.shared.properties.AdminProperties;
import ru.pulsecore.app.modules.shared.service.mail.MailStrategyRegistry;
import ru.pulsecore.app.modules.shared.service.mail.MailTypes;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final SubscriptionService subscriptionService;
    private final MailStrategyRegistry mailStrategyRegistry;
    private final AdminProperties adminProperties;

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

    public void process(Map<String, Object> body) {
        if (!EVENT_SUCCEEDED.equals(body.get(KEY_EVENT))) {
            log.debug("Ignored: event is not succeeded");
            return;
        }

        Map<String, Object> payment = getNestedMap(body, KEY_OBJECT);
        Map<String, Object> metadata = getNestedMap(payment, KEY_METADATA);

        if (!metadata.containsKey(KEY_PLAYER_ID)) {
            log.debug("Ignored: no playerId in metadata");
            return;
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
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getNestedMap(Map<String, Object> source, String key) {
        return (Map<String, Object>) source.get(key);
    }
}