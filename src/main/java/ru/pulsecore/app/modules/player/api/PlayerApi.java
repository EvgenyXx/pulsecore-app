// ==================== PlayerApi.java ====================
package ru.pulsecore.app.modules.player.api;

public final class PlayerApi {



    private PlayerApi() {}

    public static final String BASE_PATH = "/api/player";

    // ── PlayerController ──
    public static final String PROFILE = "/profile";
    public static final String CHANGE_PASSWORD = "/change-password";
    public static final String SEARCH = "/search";
    public static final String SEARCH_PARAM = "q";

    public static final String NOTIFICATIONS = "/notifications";
    public static final String NOTIFICATIONS_STATUS = "/notifications/status";

    // ── PlayerStatsController ──
    public static final String DASHBOARD = "/{id}/dashboard";
    public static final String SUM = "/sum";
    public static final String TOP_ALL = "/top/{period}";
    public static final String TOP_BY_LEAGUE = "/top/{period}/{league}";

    // ── SubscriptionController ──
    public static final String SUBSCRIPTION = "/subscription";

    // ── PaymentController ──
    public static final String PAY = "/pay";

    // ── Analytics + AI ──
    public static final String ANALYTICS = "/analytics";
    public static final String BEST_TIME = "/best-time";

    public static final String MONTHLY_INCOME = "/{id}/monthly-income";
    public static final String DAILY_INCOME = "/{id}/daily-income";

    // ── Webhook ──
    public static final String WEBHOOK = "/api/payment/webhook";


    public static final String HALLS = "/halls";



}