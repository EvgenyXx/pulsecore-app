package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.tournament.application.chat.ChatMessageFacade;

@Tag(name = "Chat Send", description = "Отправка сообщений в чат")
@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageFacade chatMessageFacade;

    @Operation(summary = "Отправить сообщение")
    @PostMapping(TournamentApi.LINEUP_ID)
    public ChatMessageDto send(
            @PathVariable(TournamentApi.PARAM_LINEUP_ID) Long lineupId,
            @RequestBody ChatMessageDto request,
            @CurrentPlayer PlayerPrincipal principal) {

        request.setPlayerId(principal.playerId());
        request.setPlayerName(principal.name());

        return chatMessageFacade.handleMessage(lineupId, request);
    }
}