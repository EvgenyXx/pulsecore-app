package ru.pulsecore.app.player.api;

public final class ProfileApi {

    private ProfileApi(){}


    public static final String BASE_PATH = "/api/profile";


    public static final String UPDATE_PLAYER = "/update";

    public static final String CHANGE_PASSWORD = "/change-password";

    public static final String NOTIFICATIONS = "/notifications";
    public static final String NOTIFICATIONS_STATUS = "/notifications/status";

    public static final String ME_THEME = "/me/theme";

    public static final String QR = "/qr";

    public static final String VERIFY_PASSWORD = "/verify-password";
}
