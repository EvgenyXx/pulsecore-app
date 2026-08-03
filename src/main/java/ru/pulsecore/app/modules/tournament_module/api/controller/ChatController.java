// ChatController.java — удалить getOnline
package ru.pulsecore.app.modules.tournament_module.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.shared.dto.PlayerData;
import ru.pulsecore.app.modules.tournament_module.api.ChatApi;
import ru.pulsecore.app.modules.tournament_module.api.dto.response.ChatMessageDto;
import ru.pulsecore.app.modules.tournament_module.service.chat.ChatFacade;
import ru.pulsecore.app.security.CurrentPlayer;
import ru.pulsecore.app.security.PlayerPrincipal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ChatApi.BASE_PATH)
@RequiredArgsConstructor
public class ChatController {

    private final ChatFacade chatFacade;

    @GetMapping(ChatApi.LINEUP_ID)
    public List<ChatMessageDto> getMessages(@PathVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId,
                                            @RequestParam(value = "after", required = false) Long after) {
        return chatFacade.getMessages(lineupId, after);
    }

    @PostMapping(ChatApi.LINEUP_ID)
    public ChatMessageDto send(@PathVariable(ChatApi.PARAM_LINEUP_ID) Long lineupId, @RequestBody ChatMessageDto msg) {
        return chatFacade.sendMessage(lineupId, msg);
    }

    @GetMapping(ChatApi.PLAYERS_SEARCH)
    public List<PlayerData> searchPlayers(@RequestParam String q) {
        return chatFacade.searchPlayers(q);
    }


    @DeleteMapping(ChatApi.MESSAGE)
    public ResponseEntity<Void> deleteMessage(
            @PathVariable(ChatApi.PARAM_MESSAGE_ID) Long messageId,
            @CurrentPlayer PlayerPrincipal principal) {
        chatFacade.deleteMessage(messageId, principal.playerId());
        return ResponseEntity.noContent().build();
    }


    @PatchMapping(ChatApi.MESSAGE)
    public ResponseEntity<Void> updateMessage(
            @PathVariable(ChatApi.PARAM_MESSAGE_ID) Long messageId,
            @CurrentPlayer PlayerPrincipal principal,
            @RequestBody ChatMessageDto msg) {
        chatFacade.updateMessage(messageId, principal.playerId(), msg.getMessage());
        return ResponseEntity.ok().build();
    }
}