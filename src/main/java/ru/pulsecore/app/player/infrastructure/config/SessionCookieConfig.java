package ru.pulsecore.app.player.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.DefaultCookieSerializer;
import ru.pulsecore.app.player.infrastructure.properties.SessionProperties;

@Configuration
@RequiredArgsConstructor
public class SessionCookieConfig {

    private final SessionProperties sessionProperties;

    @Bean
    public DefaultCookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(sessionProperties.name());
        serializer.setUseSecureCookie(sessionProperties.secure());
        serializer.setUseHttpOnlyCookie(sessionProperties.httpOnly());
        serializer.setSameSite(sessionProperties.sameSite());
        return serializer;
    }
}