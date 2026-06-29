package ru.pulsecore.app.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CacheNames {
    // Имена кешей
    public static final String PRICES = "prices";
    public static final String DASHBOARD = "dashboard";

    public static final String SUBSCRIPTION = "subscription";
    public static final String ANALYTICS = "analytics";
    public static final String MONTHLY_INCOME = "monthly_income";
    public static final String DAILY_INCOME = "daily_income";
    public static final String BEST_TIME = "best_time";
    public static final String TOP_ALL = "top-all";
    public static final String TOP_LEAGUE = "top-league";

    // Ключи
    public static final String KEY_PLAYER_ID = "#playerId";
    public static final String KEY_PERIOD = "#period";
    public static final String KEY_PERIOD_LEAGUE = "#period + ':' + #league";
}