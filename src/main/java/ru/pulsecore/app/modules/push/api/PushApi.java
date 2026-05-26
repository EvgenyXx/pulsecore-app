package ru.pulsecore.app.modules.push.api;

public final class PushApi {
    public static final String STATUS = "/status" ;

    private PushApi() {}

    public static final String BASE_PATH = "/api/push";
    public static final String VAPID_PUBLIC_KEY = "/vapid-public-key";
    public static final String SUBSCRIBE = "/subscribe";
    public static final String UNSUBSCRIBE = "/unsubscribe";

    public static final String TOGGLE = "/toggle";
    public static final String PUSH_STATUS = "/push-status";
}