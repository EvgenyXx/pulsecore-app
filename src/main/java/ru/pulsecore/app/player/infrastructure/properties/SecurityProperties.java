package ru.pulsecore.app.player.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        List<String> publicUrls,
        List<String> authenticatedUrls,
        List<String> adminUrls,
        String adminAuthority,
        int maximumSessions,
        String sessionCookieName,
        String logoutUrl,
        String loginPage,
        String apiPathPrefix,
        int apiErrorStatus,
        String apiErrorMessage,
        List<String> sessionRestoreExcludeUrls
) {
    public SecurityProperties {
        if (adminAuthority == null) adminAuthority = "ROLE_ADMIN";
        if (maximumSessions == 0) maximumSessions = 1;
        if (sessionCookieName == null) sessionCookieName = "PULSECORE_SESSION";
        if (logoutUrl == null) logoutUrl = "/api/player/logout";
        if (loginPage == null) loginPage = "/";
        if (apiPathPrefix == null) apiPathPrefix = "/api/";
        if (apiErrorStatus == 0) apiErrorStatus = 403;
        if (apiErrorMessage == null) apiErrorMessage = "Forbidden";
        if (sessionRestoreExcludeUrls == null) sessionRestoreExcludeUrls = List.of("/api/player/", "/oauth-finish.html");
    }
}