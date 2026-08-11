package ru.pulsecore.app.admin.api;

public final class AdminApi {




    private AdminApi() {}

    public static final String BASE = "/api/admin";

    /**
     * Цены подписки.
     * UPDATE_SUB_PRICE — /update, обновить цены.
     * GET_PRICE_SUB — /prices, получить текущие цены.
     * Admin.
     */
    public static final String UPDATE_SUB_PRICE = "/update";
    public static final String GET_PRICE_SUB = "/prices";

    /**
     * Роли игроков.
     * ROLES_GRANT — /players/{id}/roles/grant, выдать роль.
     * ROLES_REVOKE — /players/{id}/roles/revoke, отозвать роль.
     * ROLES — /players/{id}/roles, получить роли.
     * Admin.
     */
    public static final String ROLES_GRANT = "/players/{id}/roles/grant";
    public static final String ROLES_REVOKE = "/players/{id}/roles/revoke";
    public static final String ROLES = "/players/{id}/roles";

    /**
     * Управление игроками.
     * DELETE_PLAYER — /players/{id}, удалить игрока.
     * SEARCH_BY_NAME — /search, поиск по имени.
     * Admin.
     */
    public static final String DELETE_PLAYER = "/players/{id}";
    public static final String SEARCH_BY_NAME = "/search";

    /**
     * Подписка игрока.
     * SUBSCRIBE — /players/{id}/subscribe, выдать подписку.
     * UNSUBSCRIBE — /players/{id}/unsubscribe, отключить подписку.
     * PLAYER_SUBSCRIPTION — /players/{id}/subscription, получить подписку.
     * Admin.
     */
    public static final String SUBSCRIBE = "/players/{id}/subscribe";
    public static final String UNSUBSCRIBE = "/players/{id}/unsubscribe";
    public static final String PLAYER_SUBSCRIPTION = "/players/{id}/subscription";

    /**
     * Турниры игрока.
     * TOURNAMENT_CALCULATE — /tournaments/calculate, рассчитать результаты.
     * PLAYER_TOURNAMENTS — /players/{id}/tournaments, удалить турниры.
     * PLAYER_TOURNAMENTS_RESYNC — /players/{id}/tournaments/resync, ресинхронизация.
     * Admin.
     */
    public static final String TOURNAMENT_CALCULATE = "/tournaments/calculate";
    public static final String PLAYER_TOURNAMENTS = "/players/{id}/tournaments";
    public static final String PLAYER_TOURNAMENTS_RESYNC = "/players/{id}/tournaments/resync";

    /**
     * Рассылки.
     * BROADCAST — /broadcast, отправить сообщение всем.
     * Admin.
     */
    public static final String BROADCAST = "/broadcast";

    /**
     * Статистика.
     * PAGE_VIEWS_STATS — /stats/page-views, по страницам.
     * PAGE_VIEWS_PLAYERS — /stats/page-views/players, по игрокам.
     * Admin.
     */
    public static final String PAGE_VIEWS_STATS = "/stats/page-views";
    public static final String PAGE_VIEWS_PLAYERS = "/stats/page-views/players";
}