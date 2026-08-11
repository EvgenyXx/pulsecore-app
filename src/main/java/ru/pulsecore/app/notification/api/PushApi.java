package ru.pulsecore.app.notification.api;

public final class PushApi {

    private PushApi() {}

    /**
     * Push-уведомления.
     * STATUS — /status, статус подписки.
     * VAPID_PUBLIC_KEY — /vapid-public-key, получить VAPID ключ.
     * SUBSCRIBE — /subscribe, подписаться.
     * UNSUBSCRIBE — /unsubscribe, отписаться.
     * TOGGLE — /toggle, включить/выключить.
     * PUSH_STATUS — /push-status, проверить статус.
     * Push.
     */
    public static final String STATUS = "/status";
    public static final String BASE_PATH = "/api/push";
    public static final String VAPID_PUBLIC_KEY = "/vapid-public-key";
    public static final String SUBSCRIBE = "/subscribe";
    public static final String UNSUBSCRIBE = "/unsubscribe";
    public static final String TOGGLE = "/toggle";
    public static final String PUSH_STATUS = "/push-status";
}