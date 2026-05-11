package ru.pulsecore.app.modules.player.api;

public final class PlayerApi {
    private PlayerApi() {}

    public static final String BASE_PATH = "/api/player";

    // ── PlayerController ──────────────────────
    public static final String PROFILE = "/{id}/profile";
    public static final String CHANGE_PASSWORD = "/{id}/change-password";
    public static final String SEARCH = "/search";
    public static final String SEARCH_PARAM = "q";
    public static final String DELETE_ACCOUNT = "/{id}";
    public static final String NOTIFICATIONS = "/{id}/notifications";
    public static final String NOTIFICATIONS_STATUS = "/{id}/notifications";

    // ── PlayerStatsController ─────────────────
    public static final String DASHBOARD = "/{id}/dashboard";
    public static final String SUM = "/{id}/sum";
    public static final String TOP_WEEK = "/top/week";
    public static final String TOP_WEEK_POSITION = "/{id}/top/week";
    public static final String TOP_WEEK_POSITION_BY_LEAGUE = "/{id}/top/week/{league}";

    // ── SubscriptionController ────────────────

    public static final String SUBSCRIPTION = "/{id}/subscription";

    // ── PaymentController ─────────────────────
    public static final String PAY = "/{id}/pay";

    // ── WebhookController ─────────────────────
    public static final String WEBHOOK = "/api/payment/webhook";





    //ANALYTICS
    public static final String ANALYTICS = "/analytics";
}