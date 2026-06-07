// modules/admin/controller/BroadcastController.java
package ru.pulsecore.app.modules.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.modules.admin.AdminApi;
import ru.pulsecore.app.modules.admin.dto.BroadcastRequest;
import ru.pulsecore.app.modules.admin.service.BroadcastService;
import ru.pulsecore.app.modules.player.api.dto.MessageResponse;

@AdminController
@RequiredArgsConstructor
public class BroadcastController {

    private final BroadcastService broadcastService;

    @PostMapping(AdminApi.BROADCAST)
    public ResponseEntity<MessageResponse> broadcast(@RequestBody BroadcastRequest request) {
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Сообщение не может быть пустым"));
        }

        return ResponseEntity.ok(
                new MessageResponse(broadcastService.broadcast(request.message()).toMessage())
        );
    }
}