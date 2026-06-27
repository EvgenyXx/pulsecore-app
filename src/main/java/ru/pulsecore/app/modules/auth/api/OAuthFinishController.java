// ==================== OAuthFinishController.java ====================
package ru.pulsecore.app.modules.auth.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.modules.auth.api.dto.OAuthFinishRequest;
import ru.pulsecore.app.modules.auth.service.OAuthFinishService;

@RestController
@RequestMapping(AuthApi.BASE_PATH)
@RequiredArgsConstructor
public class OAuthFinishController {

    private final OAuthFinishService service;

    @PostMapping(AuthApi.OAUTH_FINISH)
    public ResponseEntity<String> finishOAuth(@RequestBody OAuthFinishRequest request,
                                              HttpServletRequest httpRequest) {
        service.complete(request, httpRequest);
        return ResponseEntity.ok("ok");
    }
}