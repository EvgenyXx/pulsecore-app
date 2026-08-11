package ru.pulsecore.app.tournament.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.shared.dto.response.PlayerData;
import ru.pulsecore.app.tournament.api.TournamentApi;
import ru.pulsecore.app.tournament.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.tournament.application.chat.ChatFacade;
import ru.pulsecore.app.shared.security.CurrentPlayer;
import ru.pulsecore.app.shared.security.PlayerPrincipal;

import java.util.List;

@Tag(name = "Chat", description = "Чат турниров")
@RestController
@RequestMapping(TournamentApi.BASE_PATH)
@RequiredArgsConstructor
public class ChatController {

    private final ChatFacade chatFacade;

    @Operation(summary = "Получить сообщения чата")
    @GetMapping(TournamentApi.LINEUP_ID)
    public List<ChatMessageDto> getMessages(@PathVariable(TournamentApi.PARAM_LINEUP_ID) Long lineupId,
                                            @RequestParam(value = "after", required = false) Long after) {
        return chatFacade.getMessages(lineupId, after);
    }

    @Operation(summary = "Отправить сообщение в чат")
    @PostMapping(TournamentApi.LINEUP_ID)
    public ChatMessageDto send(@PathVariable(TournamentApi.PARAM_LINEUP_ID) Long lineupId, @RequestBody ChatMessageDto msg) {
        return chatFacade.sendMessage(lineupId, msg);
    }

    @Operation(summary = "Поиск игроков для упоминаний")
    @GetMapping(TournamentApi.PLAYERS_SEARCH)
    public List<PlayerData> searchPlayers(@RequestParam String q) {
        return chatFacade.searchPlayers(q);
    }

    @Operation(summary = "Удалить сообщение")
    @DeleteMapping(TournamentApi.MESSAGE)
    public ResponseEntity<Void> deleteMessage(
            @PathVariable(TournamentApi.PARAM_MESSAGE_ID) Long messageId,
            @CurrentPlayer PlayerPrincipal principal) {
        chatFacade.deleteMessage(messageId, principal.playerId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Редактировать сообщение")
    @PatchMapping(TournamentApi.MESSAGE)
    public ResponseEntity<Void> updateMessage(
            @PathVariable(TournamentApi.PARAM_MESSAGE_ID) Long messageId,
            @CurrentPlayer PlayerPrincipal principal,
            @RequestBody ChatMessageDto msg) {
        chatFacade.updateMessage(messageId, principal.playerId(), msg.getMessage());
        return ResponseEntity.ok().build();
    }
}