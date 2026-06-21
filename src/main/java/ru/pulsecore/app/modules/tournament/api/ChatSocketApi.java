package ru.pulsecore.app.modules.tournament.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChatSocketApi {
    public static final String SEND = "/chat/{lineupId}";
    public static final String TOPIC = "/topic/chat/{lineupId}";
}