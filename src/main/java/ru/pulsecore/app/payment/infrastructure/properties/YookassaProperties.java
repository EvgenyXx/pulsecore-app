package ru.pulsecore.app.payment.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.yookassa")
public record YookassaProperties(
        int shopId,
        String secretKey,
        String returnUrl,
        String yookassaApiUrl,
        String currency,
        String orderReturnUrl
) {

}