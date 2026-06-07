package ru.pulsecore.app.modules.admin;

public final class AdminApi {
    private AdminApi() {}

    public static final String BASE = "/api/admin";
    public static final String PRICES = "/prices";
    public static final String ROLES_GRANT = "/players/{id}/roles/grant";
    public static final String ROLES_REVOKE = "/players/{id}/roles/revoke";
    public static final String ROLES = "/players/{id}/roles";

    public static final String DELETE_PLAYER = "/players/{id}";


    public static final String SUBSCRIBE = "/players/{id}/subscribe";
    public static final String UNSUBSCRIBE = "/players/{id}/unsubscribe";



    public static final String TOURNAMENT_CALCULATE = "/tournaments/calculate";

    public static final String PLAYER_SUBSCRIPTION = "/players/{id}/subscription";  // ← ДОБАВИТЬ

    public static final String PLAYER_TOURNAMENTS = "/players/{id}/tournaments";
    public static final String PLAYER_TOURNAMENTS_RESYNC = "/players/{id}/tournaments/resync";



}