package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import ru.pulsecore.app.tournament.api.ChatSocketApi;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.tournament.application.chat.ChatFacade;

@Tag(name = "Chat", description = "WebSocket чат турниров")
@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatFacade chatFacade;

    @MessageMapping(ChatSocketApi.SEND)
    public void sendMessage(@DestinationVariable(TournamentApi.PARAM_LINEUP_ID) Long lineupId, ChatMessageDto msg) {
        chatFacade.sendMessage(lineupId, msg);
    }
}