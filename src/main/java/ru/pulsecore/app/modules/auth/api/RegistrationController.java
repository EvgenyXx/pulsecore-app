package ru.pulsecore.app.modules.auth.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pulsecore.app.modules.auth.api.dto.AuthResponse;
import ru.pulsecore.app.modules.auth.api.dto.RegisterRequest;
import ru.pulsecore.app.modules.auth.api.dto.VerifyEmailRequest;
import ru.pulsecore.app.modules.auth.mapping.PlayerDtoMapper;
import ru.pulsecore.app.modules.player.api.dto.MessageResponse;
import ru.pulsecore.app.modules.player.service.auth.PlayerRegistrationService;

@RestController
@RequestMapping(AuthApi.BASE_PATH)
@RequiredArgsConstructor
public class RegistrationController {

    private final PlayerRegistrationService registrationService;
    private final PlayerDtoMapper mapper;

    @PostMapping(AuthApi.REGISTER)
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request,
                                                    HttpSession session) {
        var pending = registrationService.initiate(request.getName(), request.getEmail(), request.getPassword());
        session.setAttribute("pending", pending);
        session.setMaxInactiveInterval(600);
        return ResponseEntity.ok(new MessageResponse(AuthApi.OK));
    }

    @PostMapping(AuthApi.VERIFY_EMAIL)
    public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request,
                                                    HttpSession session,
                                                    HttpServletRequest httpRequest) {
        var pending = (PlayerRegistrationService.Pending) session.getAttribute("pending");
        if (pending == null) return ResponseEntity.status(400).build();
        if (!pending.email().equals(request.getEmail())) return ResponseEntity.status(400).build(); // ← добавил проверку email
        var player = registrationService.complete(pending, request.getCode(), httpRequest);
        session.removeAttribute("pending");
        return ResponseEntity.ok(mapper.toAuthResponse(player));
    }
}