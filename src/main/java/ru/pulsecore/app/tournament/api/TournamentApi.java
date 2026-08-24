package ru.pulsecore.app.tournament.api;

import lombok.experimental.UtilityClass;


/**
 * API эндпоинты модуля турниров.
 * Содержит пути для дашборда, зала славы, аналитики, отчетов и онлайн-трансляций.
 */
@UtilityClass
public class TournamentApi {

    public static final String BASE_PATH = "/api/tournament";


    /**
     * Матчи турнира.
     * MATCHES_BY_RESULT — матчи по ID результата игрока.
     */
    public static final String MATCHES_BY_RESULT = "/matches/by-result/{resultId}";

    /**
     * Онлайн трансляции.
     * LIVE — текущие матчи онлайн.
     * ONLINE_ALL — количество зрителей онлайн в разделе "Матчи онлайн".
     * LIVE_SELECTION_HALLS - возвращает список турниров сохраненный пользователем
     * Live.
     */
    public static final String LIVE = "/live";
    public static final String ONLINE_ALL = "/online/all";


    /**
     * Аналитика.
     * ANALYTICS — основная аналитика.
     * BEST_TIME — лучшее время.
     * MONTHLY_INCOME — доход за месяц.
     * DAILY_INCOME — доход за день.
     * Analytics.
     */
    public static final String ANALYTICS = "/analytics";
    public static final String BEST_TIME = "/best-time";
    public static final String MONTHLY_INCOME = "/monthly-income";
    public static final String DAILY_INCOME = "/daily-income";




    /**
     * Главная страница на дашборде.
     * DASHBOARD — главная страница игрока.
     * TOP_ALL — топ всех лиг за период.
     * TOP_BY_LEAGUE — топ по конкретной лиге за период.
     * Dashboard.
     */

    public static final String DASHBOARD = "/{id}/dashboard";
    public static final String TOP_ALL = "/top/{period}";
    public static final String TOP_BY_LEAGUE = "/top/{period}/{league}";


    /**
     * Сумма за период.
     * SUM — получить сумму, обновить и т.д.
     * UPDATE_RESULT — обновить результат по ID.
     * PARAM_AMOUNT, PARAM_BONUS — параметры запроса.
     * RESP_MESSAGE, RESP_OK — ключи ответа.
     * Dashboard.
     */
    public static final String SUM = "/sum";
    public static final String UPDATE_RESULT = "/result/{id}";
    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_BONUS = "bonus";
    public static final String RESP_MESSAGE = "message";
    public static final String RESP_OK = "ok";


    /**
     * Сумма за период
     * REPORTS - заказать отчет
     * REPORTS_PENDING - проверка статусы отправки
     * REPORTS_CANCEL - отменить отправку выбранного отчета
     * Dashboard
     */
    public static final String REPORTS = "/reports" ;
    public static final String REPORTS_PENDING = "/reports/pending";
    public static final String REPORTS_CANCEL = "/reports/{id}/cancel";



    /**
     * Составы и расписание.
     * ALL — все составы на дату по всем залам.
     * MY — составы по выбранным залам игрока.
     * Dashboard.
     */
    public static final String ALL = "/all";
    public static final String MY = "/my";


    /**
     * Чат турнира.
     * LINEUP_ID — получить/отправить сообщения чата по ID состава.
     * PLAYERS_SEARCH — поиск игроков для чата.
     * MESSAGE — удалить/редактировать сообщение по ID.
     * Live.
     */
    public static final String LINEUP_ID = "/{lineupId}";
    public static final String PLAYERS_SEARCH = "/players/search";
    public static final String PARAM_LINEUP_ID = "lineupId";
    public static final String MESSAGE = "/message/{id}";
    public static final String PARAM_MESSAGE_ID = "id";

    /**
     * Сравнение игроков.
     * COMPARE_PLAYERS — список игроков для сравнения за период.
     * Compare.
     */
    public static final String COMPARE_PLAYERS = "/compare/players";
    public static final String COMPARE_MATCH_STATS = "/compare/match-stats";

}