package ru.pulsecore.app.modules.player.infrastructure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private List<String> publicUrls;
    private List<String> authenticatedUrls;
    private List<String> adminUrls;
    private String adminAuthority = "ROLE_ADMIN";
    private int maximumSessions = 1;
    private String sessionCookieName = "PULSECORE_SESSION";
    private String logoutUrl = "/api/auth/logout";
    private String loginPage = "/";
    private String apiPathPrefix = "/api/";
    private int apiErrorStatus = 403;
    private String apiErrorMessage = "Forbidden";
    private List<String> sessionRestoreExcludeUrls = List.of("/api/auth/", "/oauth-finish.html");
}