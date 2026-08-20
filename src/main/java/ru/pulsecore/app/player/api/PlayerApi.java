package ru.pulsecore.app.player.api;

public final class PlayerApi {




    private PlayerApi() {}

    public static final String BASE_PATH = "/api/player";

    /**
     * Информация о подписке.
     * SUBSCRIPTION — получить статус подписки текущего игрока.
     * Profile.
     */
    public static final String SUBSCRIPTION = "/subscription";

    /**
     * Управление залами.
     * SAVE_HALLS — сохранить/получить залы для расписания турниров (GET + PUT).
     * SAVE_LIVE_HALLS — сохранить залы для лайв-трансляций (POST).
     * GET_LIVE_HALLS — получить залы для лайв-трансляций (GET).
     * Dashboard, Live.
     */
    public static final String SAVE_HALLS = "/halls";
    public static final String SAVE_LIVE_HALLS = "/live-halls";
    public static final String GET_PLAYER_HALLS = "/halls";
    public static final String GET_LIVE_HALLS = "/live/halls";




    /**
     * Авторизация и регистрация.
     * REGISTER — регистрация нового пользователя.
     * LOGIN — вход в систему.
     * LOGOUT — выход из системы.
     * ME — текущий пользователь.
     * VERIFY_EMAIL — подтверждение email.
     * FORGOT_PASSWORD — запрос на сброс пароля.
     * RESET_PASSWORD — сброс пароля.
     * OAUTH_FINISH — завершение OAuth авторизации.
     * OK — успешный ответ.
     * CODE_EXPIRED — сообщение об истекшем коде.
     * Auth.
     */
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String ME = "/me";
    public static final String VERIFY_EMAIL = "/verify-email";
    public static final String FORGOT_PASSWORD = "/forgot-password";
    public static final String RESET_PASSWORD = "/reset-password";
    public static final String OAUTH_FINISH = "/oauth-finish";
    public static final String OK = "ok";
    public static final String CODE_EXPIRED = "Код не найден или истек";


    /**
     * Профиль и настройки.
     * UPDATE_PLAYER — обновление данных профиля.
     * CHANGE_PASSWORD — смена пароля.
     * NOTIFICATIONS — настройки уведомлений.
     * NOTIFICATIONS_STATUS — статус уведомлений.
     * ME_THEME — тема оформления.
     * QR — QR-код.
     * VERIFY_PASSWORD — проверка пароля.
     * Profile.
     */
    public static final String UPDATE_PLAYER = "/update";
    public static final String CHANGE_PASSWORD = "/change-password";
    public static final String NOTIFICATIONS = "/notifications";
    public static final String NOTIFICATIONS_STATUS = "/notifications/status";
    public static final String ME_THEME = "/me/theme";
    public static final String QR = "/qr";
    public static final String VERIFY_PASSWORD = "/verify-password";


    /**
     * Онлайн трансляции.
     * BASE_PATH — базовый путь для онлайн API.
     * Online.
     */
    public static final String ONLINE = "/api/online";

}