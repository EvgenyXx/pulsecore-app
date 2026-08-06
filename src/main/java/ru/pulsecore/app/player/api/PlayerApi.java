package ru.pulsecore.app.player.api;

public final class PlayerApi {




    private PlayerApi() {}

    public static final String BASE_PATH = "/api/player";

    // ── PlayerController ──


    public static final String SEARCH = "/search";
    public static final String SEARCH_PARAM = "q";



    // ── PlayerStatsController ──

    // ── SubscriptionController ──
    public static final String SUBSCRIPTION = "/subscription";

    // ── PaymentController ──


    // ── Analytics + AI ──


    // ── Webhook ──
    public static final String WEBHOOK = "/api/payment/webhook";


    public static final String HALLS = "/halls";




    public static final String REPORT_CREATED = "Отчёт запланирован";
    public static final String REPORT_CANCELLED = "Отчёт отменён";



}