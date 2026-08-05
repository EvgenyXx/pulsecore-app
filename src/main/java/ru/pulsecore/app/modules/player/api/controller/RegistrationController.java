package ru.pulsecore.app.modules.player.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.modules.player.api.AuthApi;
import ru.pulsecore.app.modules.player.api.dto.response.AuthResponse;
import ru.pulsecore.app.modules.player.api.dto.request.RegisterRequest;
import ru.pulsecore.app.modules.player.api.dto.request.VerifyEmailRequest;
import ru.pulsecore.app.modules.player.infrastructure.persistence.mapping.PlayerDtoMapper;
import ru.pulsecore.app.modules.shared.dto.MessageResponse;
import ru.pulsecore.app.modules.player.application.auth.RegistrationFacade;

@RestController
@RequestMapping(AuthApi.BASE_PATH)
@RequiredArgsConstructor
public class RegistrationController {

    private static final String PENDING_SESSION_KEY = "pending";

    private final RegistrationFacade registrationService;
    private final PlayerDtoMapper mapper;

    @PostMapping(AuthApi.REGISTER)
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request,
                                                    HttpSession session) {
        var pending = registrationService.initiate(request.getName(), request.getEmail(), request.getPassword());
        session.setAttribute(PENDING_SESSION_KEY, pending);
        session.setMaxInactiveInterval(600);
        return ResponseEntity.ok(new MessageResponse(AuthApi.OK));
    }

    @PostMapping(AuthApi.VERIFY_EMAIL)
    public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request,
                                                    HttpSession session,
                                                    HttpServletRequest httpRequest) {
        var pending = (RegistrationFacade.Pending) session.getAttribute(PENDING_SESSION_KEY);

        var player = registrationService.complete(
                pending,
                request.getCode(),
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent")
        );
        session.removeAttribute(PENDING_SESSION_KEY);
        return ResponseEntity.ok(mapper.toAuthResponse(player));//todo убрать всю логику в сервис
    }
}