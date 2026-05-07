package ru.pulsecore.app.modules.player.api;

public final class PlayerApi {
    public static final String SUBSCRIBE = "/{id}/subscribe";
    public static final String SEARCH = "/search";

    public static final String SEARCH_PARAM = "q";
    public static final String SUBSCRIPTION = "/{id}/subscription";
    public static final String DELETE_ACCOUNT = "/{id}";
    public static final String PAY = "/{id}/pay";
    public static final String UNSUBSCRIBE = "/{id}/unsubscribe";
    public static final String GRANT_ROLE = "/{id}/roles/grant";
    public static final String REVOKE_ROLE = "/{id}/roles/revoke";

    public static final String TOP_WEEK = "/top/week";

    public static final String ROLES = "/{id}/roles";
    // PlayerApi.java - добавить константу
    public static final String NOTIFICATIONS = "/{id}/notifications";
    // PlayerApi.java
    public static final String NOTIFICATIONS_STATUS = "/{id}/notifications";

    private PlayerApi() {}

    public static final String BASE_PATH = "/api/player";
    public static final String DASHBOARD = "/{id}/dashboard";
    public static final String SUM = "/{id}/sum";
    public static final String TOURNAMENTS = "/{id}/tournaments";
    public static final String PROFILE = "/{id}/profile";
    public static final String CHANGE_PASSWORD = "/{id}/change-password";

    public static final String TOP_WEEK_POSITION = "/{id}/top/week";
}