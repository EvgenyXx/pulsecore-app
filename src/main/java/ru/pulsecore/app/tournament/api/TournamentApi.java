package ru.pulsecore.app.tournament.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TournamentApi {
    public static final String BASE_PATH = "/api/tournament";

    public static final String ADD = "/add";
    public static final String SEARCH = "/search";
    public static final String LIVE = "/live";
    public static final String PARAM_DATE = "date";

    public static final String UPDATE_RESULT = "/result/{id}";
    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_BONUS = "bonus";
    public static final String RESP_MESSAGE = "message";
    public static final String RESP_OK = "ok";

    public static final String ONLINE_ALL = "/online/all";


    /**
     * Аналитика
     */
    public static final String ANALYTICS = "/analytics";
    public static final String BEST_TIME = "/best-time";
    public static final String MONTHLY_INCOME = "/monthly-income";
    public static final String DAILY_INCOME = "/daily-income";


    /**
     * сумм контроллер
     */
    public static final String SUM = "/sum";


    /**
     * отчеты за период
     */
    public static final String REPORTS = "/reports" ;
    public static final String REPORTS_PENDING = "/reports/pending";
    public static final String REPORTS_CANCEL = "/reports/{id}/cancel";


    public static final String DASHBOARD = "/{id}/dashboard";
    public static final String TOP_ALL = "/top/{period}";
    public static final String TOP_BY_LEAGUE = "/top/{period}/{league}";




}