package ru.pulsecore.app.admin.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pulsecore.app.admin.api.AdminApi;
import ru.pulsecore.app.admin.api.dto.request.BroadcastRequest;
import ru.pulsecore.app.admin.application.BroadcastService;
import ru.pulsecore.app.shared.dto.MessageResponse;

@AdminController
@RequiredArgsConstructor
public class BroadcastController {


    private final BroadcastService broadcastService;

    @PostMapping(AdminApi.BROADCAST)
    public ResponseEntity<MessageResponse> broadcast(@RequestBody BroadcastRequest request) {
        return ResponseEntity.ok(
                new MessageResponse(broadcastService.broadcast(request.message()).toMessage())
        );
    }
}