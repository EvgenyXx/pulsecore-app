package ru.pulsecore.app.player.application.auth.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pulsecore.app.player.api.dto.request.OAuthFinishRequest;
import ru.pulsecore.app.player.application.auth.PlayerLoginService;
import ru.pulsecore.app.player.application.auth.RegistrationMailPublisher;
import ru.pulsecore.app.player.infrastructure.exception.OAuthEmailNotReceivedException;
import ru.pulsecore.app.player.infrastructure.persistence.entity.Player;
import ru.pulsecore.app.player.application.subscription.TrialActivator;


@Service
@RequiredArgsConstructor
public class OAuthFinishService {



    private final OAuthSessionExtractor sessionExtractor;
    private final OAuthPlayerBuilder playerBuilder;
    private final TrialActivator trialActivator;
    private final PlayerLoginService playerLoginService;
    private final RegistrationMailPublisher publisher;

    @Transactional
    public void complete(OAuthFinishRequest request, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession();
        var data = sessionExtractor.extract(session);
        if (data.email() == null) {
            throw new OAuthEmailNotReceivedException();
        }

        String name = (request.getLastName() + " " + request.getFirstName()).toLowerCase().trim();
        String email = data.email();
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");


        Player player = playerBuilder.build(name, email, data);
        trialActivator.activate(player);
        publisher.playerCreated(player,ip,userAgent);

        sessionExtractor.clear(session);
        playerLoginService.login(player, httpRequest);
    }
}