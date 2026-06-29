package ru.pulsecore.app.modules.tournament.api;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChatApi {
    public static final String BASE_PATH = "/api/chat";
    public static final String LINEUP_ID = "/{lineupId}";
    public static final String ONLINE = "/{lineupId}/online";
    public static final String PLAYERS_SEARCH = "/players/search";
    public static final String PARAM_LINEUP_ID = "lineupId";

    public static final String MESSAGE = "/message/{id}";
    public static final String PARAM_MESSAGE_ID = "id";
}