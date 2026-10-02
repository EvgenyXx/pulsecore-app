package ru.pulsecore.app.shop.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "shop")
public class ShopProperties {
    private Pickup pickup = new Pickup();

    @Getter
    @Setter
    public static class Pickup {
        private String city;
        private String address;
        private String phone;
    }
}