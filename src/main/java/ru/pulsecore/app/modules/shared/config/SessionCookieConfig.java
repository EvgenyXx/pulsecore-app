package ru.pulsecore.app.modules.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.DefaultCookieSerializer;
import ru.pulsecore.app.modules.shared.properties.SessionProperties;

@Configuration
@RequiredArgsConstructor
public class SessionCookieConfig {

    private final SessionProperties sessionProperties;

    @Bean
    public DefaultCookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(sessionProperties.getName());
        serializer.setUseSecureCookie(sessionProperties.isSecure());
        serializer.setUseHttpOnlyCookie(sessionProperties.isHttpOnly());
        serializer.setSameSite(sessionProperties.getSameSite());
        return serializer;
    }
}