package ru.pulsecore.app.modules.player.api;

public final class PlayerApi {

    private PlayerApi() {}

    public static final String BASE_PATH = "/api/player";

    // ── PlayerController (все через @CurrentPlayer) ──
    public static final String PROFILE = "/profile";
    public static final String CHANGE_PASSWORD = "/change-password";
    public static final String SEARCH = "/search";
    public static final String SEARCH_PARAM = "q";
    public static final String DELETE_ACCOUNT = "/account";
    public static final String NOTIFICATIONS = "/notifications";
    public static final String NOTIFICATIONS_STATUS = "/notifications/status";

    // ── PlayerStatsController (пока с {id}, не трогали) ──
    public static final String DASHBOARD = "/{id}/dashboard";
    public static final String SUM = "/{id}/sum";
    public static final String TOP_WEEK = "/top/week";
    public static final String TOP_WEEK_POSITION = "/{id}/top/week";
    public static final String TOP_WEEK_POSITION_BY_LEAGUE = "/{id}/top/week/{league}";

    // ── SubscriptionController (@CurrentPlayer) ──
    public static final String SUBSCRIPTION = "/subscription";

    // ── PaymentController (@CurrentPlayer) ──
    public static final String PAY = "/pay";

    // ── Analytics + AI ──
    public static final String ANALYTICS = "/analytics";
    public static final String CHAT = "/chat";
    public static final String WEEKLY_ANALYSIS = "/weekly-analysis";
    public static final String MONTHLY_INCOME = "/{id}/monthly-income";
    public static final String DAILY_INCOME = "/{id}/daily-income";

    // ── Webhook (отдельный) ──
    public static final String WEBHOOK = "/api/payment/webhook";
}