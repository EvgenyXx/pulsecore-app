package ru.pulsecore.app.notification.application.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pulsecore.app.player.infrastructure.exception.MailStrategyNotFoundException;
import ru.pulsecore.app.notification.application.mail.context.MailContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MailStrategyRegistry {

    private final Map<String, MailStrategy> strategies;

    public MailStrategyRegistry(List<MailStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(MailStrategy::getType, s -> s));
    }

    public void send(String type, MailContext ctx) {
        MailStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new MailStrategyNotFoundException(type);
        }
        strategy.send(ctx);

    }
}