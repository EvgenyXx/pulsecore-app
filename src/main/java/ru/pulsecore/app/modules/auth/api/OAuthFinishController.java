// ==================== OAuthFinishController.java ====================
package ru.pulsecore.app.modules.auth.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.modules.auth.service.OAuthFinishService;
import ru.pulsecore.app.modules.auth.api.dto.OAuthFinishRequest;


import java.io.IOException;

@RestController
@RequestMapping(AuthApi.BASE_PATH)
@RequiredArgsConstructor
public class OAuthFinishController {

    private final OAuthFinishService service;

    @PostMapping(AuthApi.OAUTH_FINISH)
    public void finishOAuth(@RequestBody OAuthFinishRequest request,
                            HttpServletRequest httpRequest,
                            HttpServletResponse httpResponse) throws IOException {
        service.complete(request, httpRequest, httpResponse);
    }
}