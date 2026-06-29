package ru.pulsecore.app.modules.tournament.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChatSocketApi {
    // STOMP-пути (клиент → сервер)
    public static final String SEND = "/chat/{lineupId}";
    public static final String DELETE = "/{lineupId}/delete";

    // Топики (сервер → клиент)
    public static final String TOPIC = "/topic/chat/{lineupId}";
    public static final String TOPIC_CHAT = "/topic/chat/";
    public static final String TOPIC_ONLINE = "/online";

    // JSON-ключи
    public static final String KEY_TYPE = "type";
    public static final String KEY_MESSAGE_ID = "messageId";
    public static final String KEY_MESSAGE = "message";
}