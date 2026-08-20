package ru.pulsecore.app.player.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.player.api.PlayerApi;
import ru.pulsecore.app.player.api.dto.request.OAuthFinishRequest;
import ru.pulsecore.app.player.application.auth.oauth.OAuthFinishService;

@Tag(name = "Auth", description = "Завершение OAuth авторизации")
@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class OAuthFinishController {

    private final OAuthFinishService service;

    @Operation(summary = "Завершить OAuth авторизацию")
    @PostMapping(PlayerApi.OAUTH_FINISH)
    public ResponseEntity<String> finishOAuth(@RequestBody OAuthFinishRequest request,
                                              HttpServletRequest httpRequest) {
        service.complete(request.lastName(), request.firstName(), httpRequest);
        return ResponseEntity.ok("ok");
    }
}